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

# 구글 크롬 80.0.3987.106 버전 다운로드 및 설치
RUN wget https://mirror.cs.uchicago.edu/google-chrome/pool/main/g/google-chrome-stable/google-chrome-stable_80.0.3987.106-1_amd64.deb && \
    dpkg -i google-chrome-stable_80.0.3987.106-1_amd64.deb && \
    apt-get install -f && \
    rm google-chrome-stable_80.0.3987.106-1_amd64.deb

# 크로미움 드라이버 80.0.3987.106 버전 다운로드 및 설치
RUN wget https://chromedriver.storage.googleapis.com/80.0.3987.106/chromedriver_linux64.zip && \
    unzip chromedriver_linux64.zip && \
    mv chromedriver /usr/local/bin/ && \
    rm chromedriver_linux64.zip

WORKDIR /app

# JAR 파일 복사
COPY target/eventor-back-0.0.1-SNAPSHOT.jar eventor-back.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "eventor-back.jar"]