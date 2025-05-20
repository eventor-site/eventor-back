# OpenJDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk

# 필수 패키지 설치 (bash, curl, jq, wget, unzip 등)
RUN apt-get update && apt-get install -y \
    bash curl jq wget unzip gnupg2 ca-certificates fonts-liberation libnss3 libxss1 libatk-bridge2.0-0 libgtk-3-0 \
    libx11-xcb1 libxcb1 libxcomposite1 libxcursor1 libxdamage1 libxext6 libxfixes3 libxi6 libxrandr2 libxrender1 libxtst6 \
    --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# Google Chrome 설치
RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && apt-get install -y google-chrome-stable --no-install-recommends && \
    rm -rf /var/lib/apt/lists/*

# ChromeDriver 설치
RUN set -eux; \
    DRIVER_URL=$(curl -s https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json \
      | jq -r '.channels.Stable.downloads.chromedriver[] | select(.platform=="linux64") | .url'); \
    wget -O /tmp/chromedriver.zip "$DRIVER_URL"; \
    unzip /tmp/chromedriver.zip -d /usr/local/bin; \
    chmod +x /usr/local/bin/chromedriver; \
    rm /tmp/chromedriver.zip

WORKDIR /app

# JAR 파일 복사
COPY target/eventor-back-0.0.1-SNAPSHOT.jar eventor-back.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "eventor-back.jar"]