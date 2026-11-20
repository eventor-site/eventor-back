plugins {
    java
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.eventor"
version = "0.1.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val querydslVersion = "7.7" // OpenFeign 포크(com.querydsl 패키지 그대로, Hibernate 7 지원)
val jjwtVersion = "0.13.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-aspectj")
    implementation("org.springframework.boot:spring-boot-starter-restclient") // 외부 API 호출(구글, 관광공사)
    implementation("org.springframework.boot:spring-boot-starter-flyway")   // DB 마이그레이션(src/main/resources/db/migration)
    implementation("org.flywaydb:flyway-mysql")

    implementation("com.github.ben-manes.caffeine:caffeine")                 // 로컬 캐시(Redis 대체)
    implementation("net.javacrumbs.shedlock:shedlock-spring:7.11.0")         // 스케줄 작업 중복 실행 방지(Redis 리더 선출 대체)
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:7.11.0")

    implementation("io.github.openfeign.querydsl:querydsl-jpa:$querydslVersion")
    annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:$querydslVersion:jpa")
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")

    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.2.0")

    // 핫딜 크롤러(현재 스케줄 꺼짐)와 이미지 webp 변환
    implementation("org.jsoup:jsoup:1.24.1")
    implementation("org.seleniumhq.selenium:selenium-java:4.53.0")
    implementation("io.github.bonigarcia:webdrivermanager:6.4.0")
    implementation("com.sksamuel.scrimage:scrimage-core:4.3.0")
    implementation("com.sksamuel.scrimage:scrimage-webp:4.3.0")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    runtimeOnly("com.mysql:mysql-connector-j")
    testRuntimeOnly("com.h2database:h2")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// 실행용 jar 이름을 버전 없이 고정한다(Dockerfile이 이 이름을 복사)
tasks.bootJar {
    archiveFileName = "eventor-back.jar"
}

tasks.jar {
    enabled = false
}

// 로컬 비밀값 파일은 jar에 넣지 않는다(운영은 환경 변수로 받는다)
tasks.processResources {
    exclude("application.env")
}
