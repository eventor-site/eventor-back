# OpenJDK 21 기반 이미지 사용
FROM eclipse-temurin:21-jdk

# 필수 패키지 설치
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    gnupg \
    curl \
    jq \
    fonts-liberation \
    libasound2t64 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxss1 \
    libxtst6 \
    lsb-release \
    xdg-utils \
    libgbm1 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libxcb-dri3-0 \
    libgdk-pixbuf2.0-0 \
 && apt-get clean && rm -rf /var/lib/apt/lists/*

# Google Chrome 설치 (stable channel)
RUN curl -fsSL https://dl.google.com/linux/linux_signing_key.pub | gpg --dearmor -o /usr/share/keyrings/google.gpg && \
    echo "deb [arch=amd64 signed-by=/usr/share/keyrings/google.gpg] http://dl.google.com/linux/chrome/deb/ stable main" \
    > /etc/apt/sources.list.d/google-chrome.list && \
    apt-get update && apt-get install -y google-chrome-stable

# Chrome 버전에 맞는 ChromeDriver 자동 설치
RUN CHROME_VERSION=$(google-chrome --version | grep -oP '\d+\.\d+\.\d+\.\d+') && \
    DRIVER_VERSION=$(curl -s https://googlechromelabs.github.io/chrome-for-testing/last-known-good-versions-with-downloads.json | \
      jq -r ".channels.Stable.downloads.chromedriver[] | select(.platform == \"linux64\") | .url") && \
    wget -O /tmp/chromedriver.zip "$DRIVER_VERSION" && \
    unzip /tmp/chromedriver.zip -d /usr/local/bin && \
    chmod +x /usr/local/bin/chromedriver && \
    rm /tmp/chromedriver.zip

WORKDIR /app

# JAR 파일 복사
COPY target/eventor-back-0.0.1-SNAPSHOT.jar eventor-back.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "eventor-back.jar"]