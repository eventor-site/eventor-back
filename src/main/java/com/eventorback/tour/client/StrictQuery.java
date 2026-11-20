package com.eventorback.tour.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * 쿼리 값을 모두 엄격하게 인코딩한다. 공공데이터 인증키에는 '+', '/', '=' 가 들어 있는데
 * 기본 인코딩은 '+' 를 그대로 두어 서버가 공백으로 읽고 키가 깨진다(Feign 에서 RestClient 로 옮기며 확인).
 */
final class StrictQuery {
	private StrictQuery() {
	}

	static URI toUri(UriComponentsBuilder builder) {
		var components = builder.build();
		StringBuilder query = new StringBuilder();
		components.getQueryParams().forEach((key, values) -> values.forEach(value -> {
			if (!query.isEmpty()) {
				query.append('&');
			}
			query.append(URLEncoder.encode(key, StandardCharsets.UTF_8)).append('=')
				.append(URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8));
		}));
		String base = components.getScheme() + "://" + components.getHost() + components.getPath();
		return URI.create(query.isEmpty() ? base : base + "?" + query);
	}
}
