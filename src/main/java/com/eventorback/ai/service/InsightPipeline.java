package com.eventorback.ai.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.eventorback.ai.Taxonomy;
import com.eventorback.llm.LlmException;
import com.eventorback.llm.LlmRequest;
import com.eventorback.llm.LlmResult;
import com.eventorback.llm.LlmService;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 이벤트 게시물을 Gemini 로 분석해 post_insights 에 채운다.
 *
 * <ol>
 *     <li><b>분류</b>(모든 이벤트): 제목·카테고리·기간만 보고 분야·혜택 유형·브랜드. 50건을 요청 한 번에(실측: 30건에 15초·입력 1.9천·출력 1.7천 토큰).
 *         입력이 바뀌지 않으면(source_hash) 다시 부르지 않는다.</li>
 *     <li><b>링크 읽기</b>(진행 중이고 링크가 있는 이벤트만): Gemini 가 공식 페이지를 직접 읽고(URL 읽기)
 *         진행 여부·3줄 요약·대상·참여 방법·난이도·혜택 크기. URL 하나에 약 5천 토큰이라 4건씩.
 *         종료일 없는 이벤트는 30일마다 다시 확인한다.</li>
 * </ol>
 * 사용자 요청(링크로 채우기)이 쓸 몫으로 오늘 {@link #USER_RESERVE}건은 남겨 둔다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InsightPipeline {
	static final int CLASSIFY_BATCH = 50;
	static final int DEEP_BATCH = 4;
	static final int USER_RESERVE = 30;
	private static final Pattern BRACKET = Pattern.compile("^\\s*\\[([^\\]]{1,30})]");

	private final JdbcTemplate jdbc;
	private final LlmService llm;

	public record RunResult(int requests, int processed, int saved, int skippedNoBudget, String note) {
	}

	record Candidate(long postId, String title, String category, LocalDateTime start, LocalDateTime end,
					 String endType, String link, long viewCount, String oldHash, String deepStatus,
					 LocalDateTime nextDeepAt, int deepAttempts, boolean hasInsight) {

		String hash() {
			return sha256(String.join("|", title, category, String.valueOf(start), String.valueOf(end),
				String.valueOf(endType), String.valueOf(link)));
		}

		boolean ongoing(LocalDateTime now) {
			return end == null || end.isAfter(now);
		}

		/** 링크 읽기 대상: 진행 중(또는 2주 안에 시작), 링크 있음 */
		boolean needsDeep(LocalDateTime now) {
			return ongoing(now) && link != null && link.startsWith("http")
				&& (start == null || start.isBefore(now.plusDays(14)));
		}

		String period() {
			String s = start == null ? "?" : start.toLocalDate().toString();
			String e = end == null ? (endType == null ? "종료일 없음" : endType) : end.toLocalDate().toString();
			return s + " ~ " + e;
		}
	}

	// ===================================================================== 상태

	public Map<String, Object> backlog() {
		LocalDateTime now = LocalDateTime.now();
		List<Candidate> all = load();
		long classify = all.stream().filter(c -> !c.hash().equals(c.oldHash())).count();
		long deep = all.stream().filter(c -> c.hasInsight() && deepDue(c, now)).count();
		Integer total = jdbc.queryForObject("SELECT COUNT(*) FROM post_insights WHERE domain IS NOT NULL", Integer.class);
		Integer ended = jdbc.queryForObject("SELECT COUNT(*) FROM post_insights WHERE live_status = '종료'", Integer.class);
		Integer summarized = jdbc.queryForObject("SELECT COUNT(*) FROM post_insights WHERE summary IS NOT NULL", Integer.class);
		return Map.of("events", all.size(), "classifyPending", classify, "deepPending", deep,
			"classified", total == null ? 0 : total, "summarized", summarized == null ? 0 : summarized,
			"endedByAi", ended == null ? 0 : ended);
	}

	// ===================================================================== 1. 분류

	public RunResult classifyPending(int maxRequests) {
		LocalDateTime now = LocalDateTime.now();
		List<Candidate> pending = load().stream()
			.filter(c -> !c.hash().equals(c.oldHash()))
			// 진행 중 → 최근 글 순
			.sorted(Comparator.comparing((Candidate c) -> !c.ongoing(now)).thenComparing(Candidate::postId, Comparator.reverseOrder()))
			.toList();
		int requests = 0, processed = 0, saved = 0, noBudget = 0;
		for (int i = 0; i < pending.size() && requests < maxRequests; i += CLASSIFY_BATCH) {
			List<Candidate> batch = pending.subList(i, Math.min(i + CLASSIFY_BATCH, pending.size()));
			Optional<LlmResult> result;
			try {
				result = llm.call(classifyRequest(batch), USER_RESERVE);
			} catch (LlmException e) {
				log.warn("분류 실패: {}", e.getMessage());
				return new RunResult(requests + 1, processed, saved, noBudget, e.getMessage());
			}
			if (result.isEmpty()) {
				noBudget = pending.size() - i;
				break;
			}
			requests++;
			processed += batch.size();
			saved += saveClassification(batch, result.get().json(), now);
		}
		return new RunResult(requests, processed, saved, noBudget, pending.size() + "건 대기 중이었음");
	}

	private LlmRequest classifyRequest(List<Candidate> batch) {
		String input = batch.stream()
			.map(c -> "id=%d | 제목: %s | 기존 카테고리: %s | 기간: %s".formatted(c.postId(), c.title(), c.category(), c.period()))
			.collect(Collectors.joining("\n"));
		String instruction = """
			너는 한국 이벤트·혜택 정보 사이트의 분류 담당이다. 줄마다 이벤트 하나가 있다. 모든 줄에 대해 하나씩 답한다.
			- domain: 아래 분야 중 정확히 하나. %s
			- benefits: 아래 혜택 유형 중 해당하는 것 0~3개. %s
			  (가격 할인·쿠폰=할인·쿠폰, 1+1·사은품·증정=1+1·증정, 추첨·응모·경품=응모·추첨, 체험단·리뷰어 모집=체험단,
			   출석체크·포인트 적립·스탬프=출석·적립, 신규 가입·첫 구매 혜택=신규가입, 무료 제공·무료 체험=무료)
			- brand: 이벤트를 하는 회사·브랜드의 대표 한글 이름(제목 맨 앞 [ ] 안이 보통 브랜드). 줄임말은 정식 이름으로(스벅→스타벅스). 모르면 빈 문자열.
			- confidence: 분류 확신도 0~1.
			제목에 없는 내용을 지어내지 않는다.
			""".formatted(String.join(", ", Taxonomy.DOMAINS), String.join(", ", Taxonomy.BENEFITS));
		Map<String, Object> item = Map.of("type", "OBJECT", "properties", Map.of(
				"id", Map.of("type", "INTEGER"),
				"domain", Map.of("type", "STRING", "enum", Taxonomy.DOMAINS),
				"benefits", Map.of("type", "ARRAY", "items", Map.of("type", "STRING", "enum", Taxonomy.BENEFITS)),
				"brand", Map.of("type", "STRING"),
				"confidence", Map.of("type", "NUMBER")),
			"required", List.of("id", "domain", "benefits", "brand", "confidence"));
		return new LlmRequest(llm.bulkModel(), instruction, input, Map.of("type", "ARRAY", "items", item), false);
	}

	private int saveClassification(List<Candidate> batch, JsonNode answer, LocalDateTime now) {
		Map<Long, Candidate> byId = batch.stream().collect(Collectors.toMap(Candidate::postId, Function.identity()));
		int saved = 0;
		for (JsonNode a : answer) {
			Candidate c = byId.remove(a.path("id").asLong(-1));
			String domain = a.path("domain").asString("");
			if (c == null || !Taxonomy.DOMAINS.contains(domain)) {
				continue; // 모르는 id·분야는 버린다(다음 주기에 다시)
			}
			Set<String> benefits = new LinkedHashSet<>();
			a.path("benefits").forEach(b -> {
				if (Taxonomy.BENEFITS.contains(b.asString())) {
					benefits.add(b.asString());
				}
			});
			String brand = normalizeBrand(a.path("brand").asString(""), c.title());
			double confidence = Math.max(0, Math.min(1, a.path("confidence").asDouble(0.5)));
			upsertClassification(c, domain, String.join(",", benefits), brand, confidence, now);
			saved++;
		}
		return saved;
	}

	private void upsertClassification(Candidate c, String domain, String benefits, String brand, double confidence,
		LocalDateTime now) {
		Long brandId = brand == null ? null : ensureBrand(brand);
		boolean deep = c.needsDeep(now);
		// 입력(기간·링크)이 바뀌었으면 링크 읽기도 다시
		jdbc.update("""
				INSERT INTO post_insights (post_id, domain, benefit_types, brand_id, confidence, source_hash, classified_at,
				  classify_model, deep_status)
				VALUES (?, ?, ?, ?, ?, ?, NOW(6), ?, ?)
				ON DUPLICATE KEY UPDATE domain = VALUES(domain), benefit_types = VALUES(benefit_types), brand_id = VALUES(brand_id),
				  confidence = VALUES(confidence), classified_at = NOW(6), classify_model = VALUES(classify_model),
				  deep_status = IF(source_hash <=> VALUES(source_hash), deep_status, VALUES(deep_status)),
				  deep_attempts = IF(source_hash <=> VALUES(source_hash), deep_attempts, 0),
				  next_deep_at = IF(source_hash <=> VALUES(source_hash), next_deep_at, NULL),
				  source_hash = VALUES(source_hash)
				""", c.postId(), domain, benefits.isEmpty() ? null : benefits, brandId, confidence, c.hash(),
			llm.bulkModel(), deep ? "대기" : "제외");
	}

	// ===================================================================== 2. 링크 읽기

	private static boolean deepDue(Candidate c, LocalDateTime now) {
		if (!c.needsDeep(now)) {
			return false;
		}
		if (c.deepStatus() == null || "대기".equals(c.deepStatus())) {
			return true;
		}
		boolean due = c.nextDeepAt() != null && !c.nextDeepAt().isAfter(now);
		return due && ("완료".equals(c.deepStatus()) || ("실패".equals(c.deepStatus()) && c.deepAttempts() < 3));
	}

	public RunResult deepPending(int maxRequests) {
		LocalDateTime now = LocalDateTime.now();
		List<Candidate> pending = load().stream()
			.filter(c -> c.hasInsight() && deepDue(c, now))
			// 마감이 가까운 것 → 많이 본 것
			.sorted(Comparator.comparing((Candidate c) -> c.end() == null ? LocalDateTime.MAX : c.end())
				.thenComparing(Candidate::viewCount, Comparator.reverseOrder()))
			.toList();
		int requests = 0, processed = 0, saved = 0, noBudget = 0;
		for (int i = 0; i < pending.size() && requests < maxRequests; i += DEEP_BATCH) {
			List<Candidate> batch = pending.subList(i, Math.min(i + DEEP_BATCH, pending.size()));
			Optional<LlmResult> result;
			try {
				result = llm.call(deepRequest(batch, now), USER_RESERVE);
			} catch (LlmException e) {
				log.warn("링크 읽기 실패: {}", e.getMessage());
				// 한도 초과·일시 오류(5xx)는 실패로 기록하지 않고 다음 주기에 다시
				if (!e.isRetryable()) {
					batch.forEach(c -> markDeepFailed(c, now, e.getMessage()));
				}
				return new RunResult(requests + 1, processed, saved, noBudget, e.getMessage());
			}
			if (result.isEmpty()) {
				noBudget = pending.size() - i;
				break;
			}
			requests++;
			processed += batch.size();
			saved += saveDeep(batch, result.get().json(), now);
		}
		return new RunResult(requests, processed, saved, noBudget, pending.size() + "건 대기 중이었음");
	}

	private LlmRequest deepRequest(List<Candidate> batch, LocalDateTime now) {
		String input = "오늘: " + now.toLocalDate() + "\n\n" + batch.stream()
			.map(c -> "id=%d\n제목: %s\n등록된 기간: %s\n링크: %s".formatted(c.postId(), c.title(), c.period(), c.link()))
			.collect(Collectors.joining("\n\n"));
		String instruction = """
			너는 이벤트 정보 확인 담당이다. 각 항목의 링크 페이지를 직접 읽고, 항목마다 하나씩 답한다.
			- pageRead: 페이지 내용을 실제로 읽었으면 true. 열리지 않거나 로그인·빈 페이지면 false.
			- liveStatus: 오늘 기준 이 이벤트가 진행 중이면 "진행 중", 끝났다고 페이지에 나오면(종료·마감 안내, 지난 기간, 페이지 없음) "종료", 판단할 수 없으면 "불명". 추측하지 않는다.
			- reason: 그렇게 판단한 근거 한 줄(페이지에서 본 내용).
			- summary: 이 이벤트를 처음 보는 사람이 바로 이해하도록 쓴 한국어 문장 3개(각 60자 이내). 페이지 문장을 그대로 베끼지 말고 새로 쓴다. 과장·광고 문구 금지.
			- target: 참여 대상(예: 앱 회원, 신규 가입자, 누구나). 모르면 빈 문자열.
			- howTo: 참여 방법을 한 줄로. 모르면 빈 문자열.
			- difficulty: 참여 난이도 "쉬움"(클릭·로그인 정도), "보통"(구매·가입 필요), "어려움"(조건 많음·추첨·글 작성).
			- valueNote: 혜택 크기를 한 줄로(예: "최대 50% 할인", "음료 1잔 무료"). 페이지에 없으면 빈 문자열.
			페이지에 없는 날짜·금액·조건을 지어내지 않는다. pageRead 가 false 면 summary 는 빈 배열, liveStatus 는 "불명".
			""";
		Map<String, Object> item = Map.of("type", "OBJECT", "properties", Map.of(
				"id", Map.of("type", "INTEGER"),
				"pageRead", Map.of("type", "BOOLEAN"),
				"liveStatus", Map.of("type", "STRING", "enum", Taxonomy.LIVE),
				"reason", Map.of("type", "STRING"),
				"summary", Map.of("type", "ARRAY", "items", Map.of("type", "STRING")),
				"target", Map.of("type", "STRING"),
				"howTo", Map.of("type", "STRING"),
				"difficulty", Map.of("type", "STRING", "enum", Taxonomy.DIFFICULTY),
				"valueNote", Map.of("type", "STRING")),
			"required", List.of("id", "pageRead", "liveStatus", "reason", "summary", "target", "howTo", "difficulty", "valueNote"));
		return new LlmRequest(llm.bulkModel(), instruction, input, Map.of("type", "ARRAY", "items", item), true);
	}

	private int saveDeep(List<Candidate> batch, JsonNode answer, LocalDateTime now) {
		Map<Long, Candidate> byId = batch.stream().collect(Collectors.toMap(Candidate::postId, Function.identity()));
		int saved = 0;
		for (JsonNode a : answer) {
			Candidate c = byId.remove(a.path("id").asLong(-1));
			if (c == null) {
				continue;
			}
			boolean read = a.path("pageRead").asBoolean(false);
			String live = a.path("liveStatus").asString("불명");
			if (!read || !Taxonomy.LIVE.contains(live)) {
				live = "불명";
			}
			List<String> lines = new ArrayList<>();
			if (read) {
				a.path("summary").forEach(s -> {
					String line = clip(s.asString(""), 80);
					if (!line.isBlank() && lines.size() < 3) {
						lines.add(line);
					}
				});
			}
			String difficulty = Taxonomy.DIFFICULTY.contains(a.path("difficulty").asString("")) ? a.path("difficulty").asString() : null;
			// 종료일 없는 이벤트만 30일 뒤 다시 확인. 읽지 못했으면 3일 뒤 재시도
			LocalDateTime next = !read ? now.plusDays(3) : (c.end() == null ? now.plusDays(30) : null);
			jdbc.update("""
					UPDATE post_insights SET live_status = ?, live_reason = ?, summary = ?, target = ?, how_to = ?, difficulty = ?,
					  value_note = ?, deep_status = ?, deep_attempts = deep_attempts + ?, deep_checked_at = NOW(6), next_deep_at = ?, deep_model = ?
					WHERE post_id = ?
					""", live, clip(a.path("reason").asString(""), 200), lines.isEmpty() ? null : String.join("\n", lines),
				read ? blankToNull(clip(a.path("target").asString(""), 200)) : null,
				read ? blankToNull(clip(a.path("howTo").asString(""), 300)) : null,
				read ? difficulty : null,
				read ? blankToNull(clip(a.path("valueNote").asString(""), 120)) : null,
				read ? "완료" : "실패", read ? 0 : 1, next == null ? null : Timestamp.valueOf(next), llm.bulkModel(), c.postId());
			saved++;
		}
		// 답이 빠진 항목은 실패로 보고 나중에 다시
		byId.values().forEach(c -> markDeepFailed(c, now, "응답에 없음"));
		return saved;
	}

	private void markDeepFailed(Candidate c, LocalDateTime now, String reason) {
		jdbc.update("UPDATE post_insights SET deep_status = '실패', deep_attempts = deep_attempts + 1, live_reason = ?, next_deep_at = ? WHERE post_id = ?",
			clip(reason, 200), Timestamp.valueOf(now.plusDays(3)), c.postId());
	}

	// ===================================================================== 공통

	List<Candidate> load() {
		return jdbc.query("""
				SELECT p.post_id, p.title, c.name AS category, e.start_time, e.end_time, e.end_type, e.link, p.view_count,
				  pi.post_id AS pi_id, pi.source_hash, pi.deep_status, pi.next_deep_at, pi.deep_attempts
				FROM posts p
				JOIN events e ON e.event_id = p.event_id
				JOIN categories c ON c.category_id = p.category_id
				JOIN statuses s ON s.status_id = p.status_id
				LEFT JOIN post_insights pi ON pi.post_id = p.post_id
				WHERE p.deleted_at IS NULL AND s.name = '작성됨'
				""", (rs, i) -> new Candidate(rs.getLong("post_id"), rs.getString("title"), rs.getString("category"),
			toLdt(rs.getTimestamp("start_time")), toLdt(rs.getTimestamp("end_time")), rs.getString("end_type"),
			rs.getString("link"), rs.getLong("view_count"), rs.getString("source_hash"), rs.getString("deep_status"),
			toLdt(rs.getTimestamp("next_deep_at")), rs.getInt("deep_attempts"), rs.getObject("pi_id") != null));
	}

	private Long ensureBrand(String name) {
		jdbc.update("INSERT IGNORE INTO brands (name) VALUES (?)", name);
		return jdbc.queryForObject("SELECT brand_id FROM brands WHERE name = ?", Long.class, name);
	}

	/** Gemini 가 준 이름이 없으면 제목의 [브랜드] 를 쓴다. 공백·특수문자 정리 */
	static String normalizeBrand(String fromLlm, String title) {
		String name = fromLlm == null ? "" : fromLlm.trim();
		if (name.isEmpty()) {
			Matcher m = BRACKET.matcher(title == null ? "" : title);
			name = m.find() ? m.group(1).trim() : "";
		}
		name = name.replaceAll("[\\[\\]()\"']", "").replaceAll("\\s+", " ").trim();
		return name.isEmpty() || name.length() > 40 ? null : name;
	}

	static String sha256(String s) {
		try {
			return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private static LocalDateTime toLdt(Timestamp t) {
		return t == null ? null : t.toLocalDateTime();
	}

	private static String clip(String s, int max) {
		String v = s == null ? "" : s.replaceAll("\\s+", " ").trim();
		return v.length() > max ? v.substring(0, max) : v;
	}

	private static String blankToNull(String s) {
		return s == null || s.isBlank() ? null : s;
	}

	static LocalDate weekStart(LocalDate d) {
		return d.minusDays(d.getDayOfWeek().getValue() - 1L);
	}
}
