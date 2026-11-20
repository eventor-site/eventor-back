package com.eventorback.seo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.extern.slf4j.Slf4j;

/**
 * 프론트(React) 빌드의 index.html 을 읽어 둔다. 배포로 바뀔 수 있으니 1분마다 다시 읽는다.
 * 읽지 못하면 최소한의 HTML 골격을 쓴다(검색엔진용 내용은 그대로 나간다).
 */
@Slf4j
@Component
public class IndexHtmlProvider {
	private static final Duration TTL = Duration.ofMinutes(1);
	static final String FALLBACK = """
		<!doctype html><html lang="ko"><head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
		<title>이벤터</title></head><body><div id="root"></div></body></html>""";

	private final WebProperties properties;
	private final RestClient restClient;
	private volatile String cached;
	private volatile Instant loadedAt = Instant.EPOCH;

	public IndexHtmlProvider(WebProperties properties, RestClient.Builder builder) {
		this.properties = properties;
		this.restClient = builder.build();
	}

	public String get() {
		if (cached != null && loadedAt.plus(TTL).isAfter(Instant.now())) {
			return cached;
		}
		String html = load();
		if (html != null) {
			cached = html;
			loadedAt = Instant.now();
			return html;
		}
		return cached != null ? cached : FALLBACK;
	}

	private String load() {
		if (properties.indexFile() != null && !properties.indexFile().isBlank()) {
			Path path = Path.of(properties.indexFile());
			if (Files.isRegularFile(path)) {
				try {
					return Files.readString(path, StandardCharsets.UTF_8);
				} catch (IOException e) {
					log.warn("index.html 읽기 실패: {}", e.getMessage());
				}
			}
		}
		if (properties.indexUrl() != null && !properties.indexUrl().isBlank()) {
			try {
				return restClient.get().uri(properties.indexUrl()).retrieve().body(String.class);
			} catch (Exception e) {
				log.debug("index.html 받기 실패({}): {}", properties.indexUrl(), e.getMessage());
			}
		}
		return null;
	}
}
