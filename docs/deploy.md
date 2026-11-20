# 배포·컷오버 절차

신버전(Boot 4 백엔드 + React 프론트)으로 운영을 바꾸는 순서. 한 번만 하는 작업(1~7)과 평소 배포(8)로 나눈다.
서버 작업은 사용자가 직접 한다(Claude 는 서버에 접속하지 않는다).

## 서버 폴더 구조(목표)

```
/home/ubuntu/content/eventor/
├── releases/   # React 빌드. 프론트 CI 가 rsync 로 갱신(폴더 자체는 지우지 않음)
└── uploads/    # 이미지 원본. 백엔드가 쓰고(rw) nginx 가 /uploads/ 로 서빙(ro)
    └── postimage/YYYYMMDD/...
```

| 컨테이너 | 마운트 |
|---|---|
| nginx | `/home/ubuntu/content/eventor:/srv/eventor:ro` |
| eventor-back(블루/그린 모두) | `/home/ubuntu/content/eventor/uploads:/app/uploads`, `/home/ubuntu/content/eventor/releases:/app/web:ro` |

예시: `deploy/nginx/eventor.conf`, `deploy/compose/eventor-back.example.yml`

## 1. 준비(서비스 영향 없음)

1. 구글 클라우드 콘솔 OAuth 클라이언트
   - 승인된 리디렉션 URI: `https://eventor.duckdns.org/api/auth/google/callback`, `http://localhost:9080/api/auth/google/callback`
   - 승인된 JavaScript 원본: `https://eventor.duckdns.org`, `http://localhost:9080`
2. GitHub 시크릿
   - eventor-back `ENV_FILE`: 아래 "운영 .env 항목"으로 갱신
   - eventor-front: `SERVER_HOST`, `SERVER_USER`, `SERVER_SSH_KEY` (백엔드와 같은 값)
3. 폴더 만들기: `mkdir -p /home/ubuntu/content/eventor/{releases,uploads}`

## 2. 운영 DB 백업

```bash
cd eventor-back && scripts/clone-prod-to-dev.sh --reset   # 덤프가 ~/eventor/backups/ 에 남고 eventor_dev 도 최신이 된다
```

## 3. 이미지 옮기기

지금 nginx 의 `location /postimage/ { root /image/; }` 가 보는 호스트 폴더(구버전 백엔드의 `./image`)를 새 위치로 복사한다.

```bash
# 예: 기존 폴더가 /home/ubuntu/eventor/eventor-back/image 라면
rsync -a /home/ubuntu/eventor/eventor-back/image/postimage/ /home/ubuntu/content/eventor/uploads/postimage/
```
옛 sitemap 파일(`postimage/sitemap/sitemap.xml`)은 더 쓰지 않는다(백엔드가 DB 에서 바로 만든다).

## 4. 프론트 첫 배포

eventor-front `main` 에 push → `releases/` 에 빌드가 올라간다(nginx 설정을 바꾸기 전이라 아직 사용자에게 보이지 않는다).

## 5. 백엔드 컨테이너 설정

블루/그린 compose 에 4개 컨테이너 모두 `volumes` 를 더한다(`deploy/compose/eventor-back.example.yml`).
Redis·Elasticsearch·프론트 컨테이너는 신버전에서 쓰지 않는다(6 이후 내린다).

**서버 `deploy.sh` 확인**: 헬스 체크·색 확인 주소가 예전 경로(`/back/actuator/...`)라면 새 경로로 바꾼다.
- 색 확인: `GET /api/actuator/version` → `blue` / `green`
- 헬스 체크: `GET /actuator/health` (컨테이너 포트로 직접) 또는 `GET /api/actuator/health`

## 6. 컷오버(짧은 점검 시간 권장)

1. 백업(2) 직후 이미지 동기화를 한 번 더(`rsync` 재실행)
2. nginx 설정 교체(`deploy/nginx/eventor.conf`) → `docker exec nginx nginx -t && docker exec nginx nginx -s reload`
   - 이 시점부터 `/` 는 React, `/api` 는 백엔드로 간다. 구버전 백엔드는 `/back` 이라 잠시 API 가 비게 되므로 바로 3 을 진행한다
3. eventor-back `main` 에 push → CI 가 빌드·배포
   - 첫 기동 때 Flyway 가 운영 DB 에 기준선(V1)을 표시하고 V2~V5 를 적용한다(V5: 1년 안에 로그인한 휴면 회원 활성화)(게시물 FULLTEXT 인덱스 포함 20초 안팎)
   - V3·V4 가 이미지 주소를 `/uploads/...` 로 바꾼다
4. 확인
   - `https://eventor.duckdns.org/` 화면, 로그인(구글·아이디), 글 상세, 이미지
   - `curl -s https://eventor.duckdns.org/api/actuator/version` 이 blue/green 을 돌려준다
   - `https://eventor.duckdns.org/sitemap.xml`, `/posts/2` 의 HTML 에 제목·본문이 있는지
5. 구버전 프론트 컨테이너, Redis, Elasticsearch 컨테이너 중지
6. 구글 서치 콘솔·네이버 서치어드바이저에 `sitemap.xml` 다시 제출(사이트맵 색인 형식으로 바뀜)

### 되돌리기

nginx 설정을 예전 것으로 되돌리고 구버전 컨테이너를 다시 띄운 뒤, 2 의 덤프로 DB 를 복원한다(V2~V4 이후 생긴 글·댓글은 사라지므로 되돌리기는 컷오버 직후에만).

## 운영 .env 항목

```
PROD_SERVER_PORT=…
PROD_MYSQL_URL=jdbc:mysql://mysql-server-1:3306/eventor
MYSQL_USERNAME=…
MYSQL_PASSWORD=…
JWT_SECRET=…                 # 32바이트 이상. 2026-11-20 새로 생성(로컬 application.env 값). 바꾸면 전원 재로그인
GOOGLE_CLIENT_ID=754902354311-s71mupscoljotb2254q15qurtd2cbclv.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=…       # 새 보안 비밀번호
GOOGLE_EMAIL_USERNAME=…
GOOGLE_EMAIL_PASSWORD=…
TOUR_KEY=…
GEMINI_API_KEY=…             # Google AI Studio 이벤터 전용 프로젝트 키(무료 등급)
# 선택: GEMINI_BULK_MODEL(기본 gemini-3.5-flash-lite), GEMINI_QUALITY_MODEL(기본 gemini-3.8-flash), GEMINI_JOBS_ENABLED(기본 true)
# 컷오버 때 Flyway V9 가 AI 테이블을 만들고, 자동 작업이 운영 이벤트를 하루 몫 안에서 차례로 분석한다(약 하루)
# 준비 중(키 받으면 추가): KOPIS_KEY, FINLIFE_KEY, KOBIS_KEY, COUPANG_ACCESS_KEY, COUPANG_SECRET_KEY
```
더 이상 쓰지 않음: `GEOCODER_KEY`(주변 관광지 제거), `REDIS_PASSWORD`, `NAVER_*`, `KAKAO_*`, `EUREKA_*`, `DEV_MYSQL_URL`(로컬 전용), `PROD_MYSQL_ROUTER_*`

## 8. 평소 배포

| 저장소 | 방법 |
|---|---|
| eventor-back | `main` push → Gradle 빌드(테스트 포함) → Docker Hub → 서버 `deploy.sh` 가 블루/그린 전환 |
| eventor-front | `main` push → Vite 빌드 → `releases/` rsync → nginx reload |

DB 스키마를 바꿀 때는 `src/main/resources/db/migration/V{다음 번호}__설명.sql` 을 더한다. 로컬(eventor_dev)에서 먼저 적용해 보고 배포한다. 이미 적용된 파일은 고치지 않는다.
