package com.eventorback.ai.service;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.eventorback.ai.Taxonomy;
import com.eventorback.global.exception.BadRequestException;
import com.eventorback.llm.LlmException;
import com.eventorback.llm.LlmRequest;
import com.eventorback.llm.LlmResult;
import com.eventorback.llm.LlmService;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;

/**
 * 글쓰기 "링크로 채우기"(G3). 회원이 붙인 공개 이벤트 페이지를 Gemini 가 읽고 제목·기간·분야·요약을 채운다.
 * 사용자 요청이라 배경 작업보다 먼저 쓴다(남겨 둔 몫 사용). 회원당 하루 5번.
 * 회원 정보는 보내지 않는다(URL 하나만).
 */
@Service
@RequiredArgsConstructor
public class LinkPreviewService {
	static final int PER_USER_DAILY = 5;
	private final LlmService llm;
	private final Map<String, Integer> usage = new ConcurrentHashMap<>();

	public Map<String, Object> preview(Long userId, String url) {
		URI uri = validate(url);
		String key = userId + ":" + LocalDate.now();
		if (usage.getOrDefault(key, 0) >= PER_USER_DAILY) {
			throw new BadRequestException("링크로 채우기는 하루 " + PER_USER_DAILY + "번까지 쓸 수 있어요.");
		}
		String instruction = """
			너는 이벤트 등록 도우미다. 링크 페이지를 직접 읽고 이벤트 정보를 채운다. 페이지에 없는 내용은 비워 둔다(지어내지 않는다).
			- pageRead: 페이지를 실제로 읽었으면 true
			- title: "[브랜드] 이벤트 이름" 형식, 50자 이내
			- brand: 이벤트를 하는 회사·브랜드 한글 이름
			- startDate, endDate: YYYY-MM-DD. 없으면 빈 문자열
			- endType: 종료일이 없을 때만 "상시", "별도 공지 시", "별도 소진 시" 중 하나, 종료일이 있으면 "없음"(빈 enum 값은 Gemini 가 거부한다)
			- domain, benefits: 주어진 값 중에서
			- summary: 처음 보는 사람이 이해할 문장 3개(각 60자 이내, 원문을 베끼지 말고 새로 씀)
			""";
		Map<String, Object> schema = Map.of("type", "OBJECT", "properties", Map.of(
				"pageRead", Map.of("type", "BOOLEAN"),
				"title", Map.of("type", "STRING"),
				"brand", Map.of("type", "STRING"),
				"startDate", Map.of("type", "STRING"),
				"endDate", Map.of("type", "STRING"),
				"endType", Map.of("type", "STRING", "enum", List.of("없음", "상시", "별도 공지 시", "별도 소진 시")),
				"domain", Map.of("type", "STRING", "enum", Taxonomy.DOMAINS),
				"benefits", Map.of("type", "ARRAY", "items", Map.of("type", "STRING", "enum", Taxonomy.BENEFITS)),
				"summary", Map.of("type", "ARRAY", "items", Map.of("type", "STRING"))),
			"required", List.of("pageRead", "title", "brand", "startDate", "endDate", "endType", "domain", "benefits", "summary"));
		Optional<LlmResult> result;
		try {
			result = llm.call(new LlmRequest(llm.bulkModel(), instruction, "링크: " + uri, schema, true), 0);
		} catch (LlmException e) {
			throw new BadRequestException(e.isRateLimited()
				? "지금은 자동 채우기 사용량이 많아요. 잠시 뒤에 다시 해 주세요." : "페이지를 읽지 못했어요. 직접 입력해 주세요.");
		}
		if (result.isEmpty()) {
			throw new BadRequestException("오늘 자동 채우기 사용량을 다 썼어요. 직접 입력해 주세요.");
		}
		JsonNode a = result.get().json();
		if (!a.path("pageRead").asBoolean(false)) {
			throw new BadRequestException("페이지를 읽지 못했어요(로그인이 필요하거나 막힌 페이지). 직접 입력해 주세요.");
		}
		usage.merge(key, 1, Integer::sum); // 읽기에 성공한 것만 하루 횟수에서 뺀다
		Map<String, Object> o = new LinkedHashMap<>();
		o.put("title", clip(a.path("title").asString(""), 80));
		o.put("brand", clip(a.path("brand").asString(""), 40));
		o.put("startDate", date(a.path("startDate").asString("")));
		o.put("endDate", date(a.path("endDate").asString("")));
		String endType = a.path("endType").asString("");
		o.put("endType", "없음".equals(endType) ? null : blank(endType));
		o.put("domain", Taxonomy.DOMAINS.contains(a.path("domain").asString("")) ? a.path("domain").asString() : null);
		Set<String> benefits = new LinkedHashSet<>();
		a.path("benefits").forEach(b -> {
			if (Taxonomy.BENEFITS.contains(b.asString(""))) {
				benefits.add(b.asString());
			}
		});
		o.put("benefits", benefits);
		List<String> summary = new ArrayList<>();
		a.path("summary").forEach(s -> {
			if (!s.asString("").isBlank() && summary.size() < 3) {
				summary.add(clip(s.asString(), 80));
			}
		});
		o.put("summary", summary);
		o.put("link", uri.toString());
		o.put("remainingToday", PER_USER_DAILY - usage.getOrDefault(key, 0));
		return o;
	}

	private static URI validate(String url) {
		try {
			URI uri = URI.create(url == null ? "" : url.trim());
			String host = uri.getHost();
			if (!("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) || host == null
				|| host.equals("localhost") || host.matches("^(\\d+\\.){3}\\d+$") || !host.contains(".")) {
				throw new IllegalArgumentException();
			}
			return uri;
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("http(s):// 로 시작하는 이벤트 페이지 주소를 넣어 주세요.");
		}
	}

	private static String date(String s) {
		try {
			return s == null || s.isBlank() ? null : LocalDate.parse(s.trim()).toString();
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	private static String blank(String s) {
		return s == null || s.isBlank() ? null : s;
	}

	private static String clip(String s, int max) {
		String v = s == null ? "" : s.replaceAll("\\s+", " ").trim();
		return v.length() > max ? v.substring(0, max) : v;
	}
}
