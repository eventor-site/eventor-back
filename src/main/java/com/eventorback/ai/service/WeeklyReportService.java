package com.eventorback.ai.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.eventorback.llm.LlmRequest;
import com.eventorback.llm.LlmResult;
import com.eventorback.llm.LlmService;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 주간 혜택 리포트. 숫자는 DB에서 세고(정확), Gemini 는 그 숫자와 이벤트 목록을 보고 해설·추천만 쓴다.
 * 주 1회(월요일 아침)라 품질 모델(하루 8회 몫)을 쓰고, 그 몫이 없으면 대량 모델로 쓴다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeeklyReportService {
	private static final ObjectMapper MAPPER = JsonMapper.builder().build();
	private final JdbcTemplate jdbc;
	private final LlmService llm;
	private final InsightQueryService queries;

	@CacheEvict(cacheNames = "cache", key = "'insightsOverview'", cacheManager = "cacheManager")
	public Optional<Map<String, Object>> generate(boolean force) {
		LocalDate week = InsightPipeline.weekStart(LocalDate.now(ZoneId.of("Asia/Seoul")));
		Integer exists = jdbc.queryForObject("SELECT COUNT(*) FROM insight_reports WHERE week_start = ?", Integer.class, week);
		if (exists != null && exists > 0 && !force) {
			return Optional.empty();
		}
		Map<String, Object> stats = statsFor();
		List<Map<String, Object>> events = jdbc.queryForList("""
			SELECT p.post_id AS id, p.title, pi.domain, pi.benefit_types AS benefits, pi.value_note AS valueNote, pi.difficulty,
			  e.end_time AS endTime, p.view_count AS views
			""" + InsightQueryService.FROM_EVENTS + " WHERE " + InsightQueryService.ONGOING + """
			ORDER BY (p.created_at >= NOW() - INTERVAL 14 DAY) DESC, (e.end_time IS NOT NULL AND e.end_time <= NOW() + INTERVAL 14 DAY) DESC,
			  p.view_count DESC LIMIT 40
			""");
		String input;
		try {
			input = "통계: " + MAPPER.writeValueAsString(stats) + "\n\n진행 중 이벤트(최대 40개): " + MAPPER.writeValueAsString(events);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		String instruction = """
			너는 이벤트·혜택 정보 사이트 "이벤터"의 에디터다. 주어진 통계와 이벤트 목록만 근거로 이번 주 혜택 리포트를 쓴다.
			- headline: 이번 주를 한 줄로(40자 이내, 숫자 하나 이상 포함).
			- body: 3개 문단(각 2~3문장). 1) 이번 주 흐름(어느 분야·혜택 유형이 많은지, 숫자 인용) 2) 마감 임박·놓치기 쉬운 것 3) 참여 팁.
			  통계에 없는 숫자, 목록에 없는 이벤트를 지어내지 않는다. 광고 문구·과장 금지. 존댓말.
			- highlights: 목록에서 고른 추천 이벤트 3~5개. id 는 목록의 id 그대로, reason 은 고른 이유 한 줄(30자 이내).
			""";
		Map<String, Object> schema = Map.of("type", "OBJECT", "properties", Map.of(
				"headline", Map.of("type", "STRING"),
				"body", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
				"highlights", Map.of("type", "ARRAY", "items", Map.of("type", "OBJECT", "properties", Map.of(
					"id", Map.of("type", "INTEGER"), "reason", Map.of("type", "STRING")), "required", List.of("id", "reason")))),
			"required", List.of("headline", "body", "highlights"));

		Optional<LlmResult> result;
		String model = llm.qualityModel();
		try {
			result = llm.call(new LlmRequest(llm.qualityModel(), instruction, input, schema, false), 2);
		} catch (com.eventorback.llm.LlmException e) {
			if (!e.isRetryable()) {
				throw e;
			}
			log.info("주간 리포트: 품질 모델 일시 오류({}), 대량 모델로 작성", e.getMessage());
			result = Optional.empty();
		}
		if (result.isEmpty()) {
			result = llm.call(new LlmRequest(llm.bulkModel(), instruction, input, schema, false), InsightPipeline.USER_RESERVE);
			model = llm.bulkModel();
		}
		if (result.isEmpty()) {
			log.info("주간 리포트: 오늘 Gemini 몫이 없어 다음에 다시");
			return Optional.empty();
		}
		JsonNode a = result.get().json();
		Map<Long, String> titles = events.stream().collect(Collectors.toMap(e -> ((Number)e.get("id")).longValue(), e -> (String)e.get("title"), (x, y) -> x));
		List<Map<String, Object>> highlights = new ArrayList<>();
		Set<Long> seen = new HashSet<>();
		a.path("highlights").forEach(h -> {
			long id = h.path("id").asLong(-1);
			if (titles.containsKey(id) && seen.add(id) && highlights.size() < 5) {
				highlights.add(Map.of("postId", id, "title", titles.get(id), "reason", clip(h.path("reason").asString(""), 60)));
			}
		});
		List<String> paragraphs = new ArrayList<>();
		a.path("body").forEach(p -> {
			if (!p.asString("").isBlank() && paragraphs.size() < 4) {
				paragraphs.add(clip(p.asString(), 400));
			}
		});
		String headline = clip(a.path("headline").asString("이번 주 혜택 리포트"), 120);
		try {
			jdbc.update("""
					INSERT INTO insight_reports (week_start, headline, body, highlights, stats_json, model) VALUES (?, ?, ?, ?, ?, ?)
					ON DUPLICATE KEY UPDATE headline = VALUES(headline), body = VALUES(body), highlights = VALUES(highlights),
					  stats_json = VALUES(stats_json), model = VALUES(model), created_at = NOW(6)
					""", week, headline, String.join("\n\n", paragraphs), MAPPER.writeValueAsString(highlights),
				MAPPER.writeValueAsString(stats), model);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return Optional.ofNullable(queries.latestReport());
	}

	private Map<String, Object> statsFor() {
		Map<String, Object> o = queries.overview();
		return Map.of("ongoing", o.get("ongoing"), "newThisWeek", o.get("newThisWeek"), "byDomain", o.get("byDomain"),
			"byBenefit", o.get("byBenefit"), "topBrands", o.get("topBrands"), "endingSoon", o.get("endingSoon"));
	}

	private static String clip(String s, int max) {
		String v = s == null ? "" : s.trim();
		return v.length() > max ? v.substring(0, max) : v;
	}
}
