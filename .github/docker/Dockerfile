# Java 25 실행 환경(빌드는 GitHub Actions에서 끝내고 jar만 넣는다)
FROM eclipse-temurin:25-jre

# Chrome 은 넣지 않는다(2026-11-20 제거). 핫딜 크롤러(Selenium)는 스케줄이 꺼져 있고, 다시 켜려면 Chrome 설치를 되살려야 한다.

# 서버 deploy.sh 가 컨테이너 안에서 헬스 체크를 할 수 있도록 curl 만 둔다
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

ENV TZ=Asia/Seoul
WORKDIR /app

# ./gradlew bootJar 결과물(build.gradle.kts 에서 이름 고정)
COPY build/libs/eventor-back.jar eventor-back.jar

# 업로드 파일 저장 루트(eventor.storage.root=./uploads → /app/uploads). 서버 compose 에서 호스트 폴더를 마운트한다
VOLUME ["/app/uploads"]

ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "eventor-back.jar"]
