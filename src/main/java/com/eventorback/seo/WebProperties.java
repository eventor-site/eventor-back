package com.eventorback.seo;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SEO HTML 에 쓸 프론트 index.html 위치(eventor.web.*).
 *
 * @param indexFile 프론트 빌드의 index.html 경로(작업 폴더 기준). 운영 컨테이너는 /home/ubuntu/content/eventor 를 ./web 에 읽기 전용 마운트(→ ./web/releases/index.html)
 * @param indexUrl  파일이 없을 때 index.html 을 받아 올 주소(로컬 개발: Vite 9080). 비우면 쓰지 않는다
 */
@ConfigurationProperties("eventor.web")
public record WebProperties(String indexFile, String indexUrl) {
}
