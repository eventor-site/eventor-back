#!/usr/bin/env bash
# 서버 /home/ubuntu/eventor/eventor-back/deploy.sh (CI 가 `cd eventor/eventor-back && source deploy.sh` 로 실행)
# 블루/그린 전환. 기존 스크립트와 구조·파일 이름은 같고, 2026-11 마이그레이션에서 바꾼 곳(★)만 다르다.
#   ★ "컨테이너가 Up" 이 아니라 두 인스턴스의 HTTP 헬스 체크(/api/actuator/health)가 성공해야 nginx 를 넘긴다.
#     새 버전은 첫 기동 때 Flyway 가 운영 DB 를 바꾸느라(이미지 12만 건 주소·FULLTEXT 인덱스) 20초 이상 걸린다.
#     Up 만 보고 넘기면 그동안 502, 기동에 실패하면 옛 컨테이너까지 내려 장애가 난다.
#   ★ 기다리는 시간 60초 → 180초.
#   ★ 실패하면 nginx·옛 컨테이너는 그대로 두고(서비스 유지) 새 컨테이너 로그를 보여 준 뒤 내린다.
# 포트: blue 8101/8102, green 8103/8104 (백엔드 /api/actuator/version 도 이 포트로 색을 판단한다)

# Blue 를 기준으로 현재 떠있는 컨테이너를 체크한다.
EXIST_BLUE=$(docker-compose -p eventor-back-blue -f docker-compose.blue.yml ps | grep Up)

# 컨테이너 스위칭(새 색을 띄운다)
if [ -z "$EXIST_BLUE" ]; then
    echo "blue up"
    docker-compose -p eventor-back-blue -f docker-compose.blue.yml down
    sudo docker rmi dlrudgjs104/eventor-back:blue-1
    sudo docker rmi dlrudgjs104/eventor-back:blue-2
    docker-compose -p eventor-back-blue -f docker-compose.blue.yml up -d
    BEFORE_COMPOSE_COLOR="green"
    AFTER_COMPOSE_COLOR="blue"
    PORTS="8101 8102"
else
    echo "green up"
    docker-compose -p eventor-back-green -f docker-compose.green.yml down
    sudo docker rmi dlrudgjs104/eventor-back:green-1
    sudo docker rmi dlrudgjs104/eventor-back:green-2
    docker-compose -p eventor-back-green -f docker-compose.green.yml up -d
    BEFORE_COMPOSE_COLOR="blue"
    AFTER_COMPOSE_COLOR="green"
    PORTS="8103 8104"
fi

# ★ 두 인스턴스가 모두 HTTP 로 응답할 때까지 최대 180초(5초 × 36) 기다린다
echo "Checking new containers (health: /api/actuator/health on $PORTS)..."
for i in {1..36}; do
    READY=0
    for P in $PORTS; do
        if curl -sf --max-time 3 "http://localhost:$P/api/actuator/health" > /dev/null; then
            READY=$((READY + 1))
        fi
    done

    if [ "$READY" -eq 2 ]; then
        echo "New containers are healthy!"

        # Nginx upstream 파일 업데이트
        if [ "$AFTER_COMPOSE_COLOR" == "blue" ]; then
            cat > /home/ubuntu/nginx/eventor-backend_upstream.conf <<EOF
upstream eventor-backend_upstream {
    server eventor-back-blue-1:8101;
    server eventor-back-blue-2:8102;
}
EOF
        else
            cat > /home/ubuntu/nginx/eventor-backend_upstream.conf <<EOF
upstream eventor-backend_upstream {
    server eventor-back-green-1:8103;
    server eventor-back-green-2:8104;
}
EOF
        fi

        # Nginx 설정 검사 후 reload
        docker exec nginx /bin/bash -c "nginx -t && nginx -s reload"
        echo "Nginx upstream switched to $AFTER_COMPOSE_COLOR"

        # 이전 컨테이너 종료
        docker-compose -p eventor-back-${BEFORE_COMPOSE_COLOR} -f docker-compose.${BEFORE_COMPOSE_COLOR}.yml down
        echo "$BEFORE_COMPOSE_COLOR down"
        exit 0
    fi
    echo "Attempt $i/36: $READY/2 healthy, waiting 5 seconds..."
    sleep 5
done

# ★ 실패: 옛 색과 nginx 는 그대로(서비스 유지), 새 색 로그를 남기고 내린다
echo "The new containers did not become healthy within 180 seconds. Keeping $BEFORE_COMPOSE_COLOR."
docker-compose -p eventor-back-${AFTER_COMPOSE_COLOR} -f docker-compose.${AFTER_COMPOSE_COLOR}.yml logs --tail=80
docker-compose -p eventor-back-${AFTER_COMPOSE_COLOR} -f docker-compose.${AFTER_COMPOSE_COLOR}.yml down
exit 1
