package com.eventorback.ai.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.eventorback.ai.Taxonomy;
import com.eventorback.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 화면용 인사이트 조회. Gemini 를 부르지 않는다(post_insights·통계 SQL만).
 * "진행 중" = 종료일이 없거나 지나지 않았고, Gemini 가 링크를 읽고 종료로 판정하지 않은 이벤트(검색 화면과 같은 기준).
 */
@Service
@RequiredArgsConstructor
public class InsightQueryService {
	private final JdbcTemplate jdbc;

	/** 진행 중 이벤트 조건(별칭: p, e, s, pi) */
	static final String ONGOING = """
		p.deleted_at IS NULL AND s.name = '작성됨'
		AND (e.end_time IS NULL OR (COALESCE(e.start_time, NOW()) <= NOW() AND e.end_time > NOW()))
		AND (pi.live_status IS NULL OR pi.live_status <> '종료')
		""";
	static final String FROM_EVENTS = """
		FROM posts p JOIN events e ON e.event_id = p.event_id JOIN statuses s ON s.status_id = p.status_id
		LEFT JOIN post_insights pi ON pi.post_id = p.post_id LEFT JOIN brands b ON b.brand_id = pi.brand_id
		""";
	static final String THUMB = "(SELECT i.url FROM images i WHERE i.post_id = p.post_id AND i.is_thumbnail = 1 LIMIT 1)";

	// ===================================================================== 게시물

	public Map<String, Object> postInsight(Long postId) {
		List<Map<String, Object>> rows = jdbc.queryForList("""
			SELECT pi.domain, pi.benefit_types, b.name AS brand, pi.summary, pi.target, pi.how_to, pi.difficulty, pi.value_note,
			  pi.live_status, pi.live_reason, pi.deep_checked_at, pi.classified_at
			FROM post_insights pi LEFT JOIN brands b ON b.brand_id = pi.brand_id WHERE pi.post_id = ?
			""", postId);
		Map<String, Object> result = new LinkedHashMap<>();
		if (rows.isEmpty()) {
			result.put("analyzed", false);
			result.put("similar", List.of());
			return result;
		}
		Map<String, Object> r = rows.getFirst();
		result.put("analyzed", true);
		result.put("domain", r.get("domain"));
		result.put("benefits", split((String)r.get("benefit_types")));
		result.put("brand", r.get("brand"));
		result.put("summary", r.get("summary") == null ? List.of() : List.of(((String)r.get("summary")).split("\n")));
		result.put("target", r.get("target"));
		result.put("howTo", r.get("how_to"));
		result.put("difficulty", r.get("difficulty"));
		result.put("valueNote", r.get("value_note"));
		result.put("liveStatus", r.get("live_status"));
		result.put("liveReason", r.get("live_reason"));
		result.put("checkedAt", r.get("deep_checked_at"));
		result.put("similar", similar(postId, (String)r.get("domain"), (String)r.get("brand"), split((String)r.get("benefit_types"))));
		return result;
	}

	/** 같은 분야 진행 중 이벤트 중 혜택 유형·브랜드가 겹치는 것 우선 */
	private List<Map<String, Object>> similar(Long postId, String domain, String brand, List<String> benefits) {
		if (domain == null) {
			return List.of();
		}
		String benefitScore = benefits.isEmpty() ? "0" : String.join(" + ",
			benefits.stream().map(b -> "(FIND_IN_SET(?, pi.benefit_types) > 0)").toList());
		List<Object> args = new ArrayList<>(benefits);
		args.add(brand == null ? "" : brand);
		args.add(domain);
		args.add(postId);
		return jdbc.queryForList("""
			SELECT p.post_id AS postId, p.title, e.end_time AS endTime, e.end_type AS endType, %s AS imageUrl, b.name AS brand
			%s WHERE %s AND pi.domain = ? AND p.post_id <> ?
			ORDER BY (%s) * 2 + COALESCE(b.name = ?, 0) DESC, p.view_count DESC LIMIT 6
			""".formatted(THUMB, FROM_EVENTS, ONGOING, benefitScore),
			reorder(args, benefits.size()));
	}

	/** SQL 의 ? 순서(도메인, postId, 혜택들, 브랜드)에 맞춘다 */
	private static Object[] reorder(List<Object> args, int benefitCount) {
		List<Object> benefits = args.subList(0, benefitCount);
		Object brand = args.get(benefitCount);
		Object domain = args.get(benefitCount + 1);
		Object postId = args.get(benefitCount + 2);
		List<Object> ordered = new ArrayList<>();
		ordered.add(domain);
		ordered.add(postId);
		ordered.addAll(benefits);
		ordered.add(brand);
		return ordered.toArray();
	}

	// ===================================================================== 트렌드

	@Cacheable(cacheNames = "cache", key = "'insightsOverview'", cacheManager = "cacheManager")
	public Map<String, Object> overview() {
		Map<String, Object> o = new LinkedHashMap<>();
		o.put("ongoing", jdbc.queryForObject("SELECT COUNT(*) " + FROM_EVENTS + " WHERE " + ONGOING, Long.class));
		o.put("newThisWeek", jdbc.queryForObject("SELECT COUNT(*) " + FROM_EVENTS
			+ " WHERE p.deleted_at IS NULL AND s.name = '작성됨' AND p.created_at >= NOW() - INTERVAL 7 DAY", Long.class));
		o.put("endingSoon", jdbc.queryForList("SELECT p.post_id AS postId, p.title, e.end_time AS endTime, " + THUMB + " AS imageUrl "
			+ FROM_EVENTS + " WHERE " + ONGOING + " AND e.end_time <= NOW() + INTERVAL 14 DAY ORDER BY e.end_time LIMIT 6"));
		o.put("endedByAi", jdbc.queryForObject("SELECT COUNT(*) FROM post_insights WHERE live_status = '종료'", Long.class));

		Map<String, Long> domains = new LinkedHashMap<>();
		Taxonomy.DOMAINS.forEach(d -> domains.put(d, 0L));
		jdbc.queryForList("SELECT pi.domain AS d, COUNT(*) AS n " + FROM_EVENTS + " WHERE " + ONGOING
			+ " AND pi.domain IS NOT NULL GROUP BY pi.domain").forEach(r -> domains.put((String)r.get("d"), ((Number)r.get("n")).longValue()));
		o.put("byDomain", domains);

		Map<String, Long> benefits = new LinkedHashMap<>();
		Taxonomy.BENEFITS.forEach(b -> benefits.put(b, 0L));
		jdbc.queryForList("SELECT pi.benefit_types AS t " + FROM_EVENTS + " WHERE " + ONGOING + " AND pi.benefit_types IS NOT NULL", String.class)
			.forEach(t -> split(t).forEach(b -> benefits.merge(b, 1L, Long::sum)));
		o.put("byBenefit", benefits);

		o.put("difficulty", jdbc.queryForList("SELECT pi.difficulty AS name, COUNT(*) AS count " + FROM_EVENTS + " WHERE " + ONGOING
			+ " AND pi.difficulty IS NOT NULL GROUP BY pi.difficulty"));
		o.put("topBrands", jdbc.queryForList("SELECT b.name, COUNT(*) AS ongoing " + FROM_EVENTS + " WHERE " + ONGOING
			+ " AND b.name IS NOT NULL GROUP BY b.name ORDER BY ongoing DESC, b.name LIMIT 12"));
		o.put("easyPicks", jdbc.queryForList("SELECT p.post_id AS postId, p.title, pi.value_note AS valueNote, b.name AS brand, "
			+ THUMB + " AS imageUrl " + FROM_EVENTS + " WHERE " + ONGOING
			+ " AND pi.difficulty = '쉬움' AND pi.value_note IS NOT NULL ORDER BY p.view_count DESC LIMIT 6"));
		// 월별 신규 이벤트(최근 12개월)
		Map<String, Long> monthly = new TreeMap<>();
		jdbc.queryForList("SELECT DATE_FORMAT(p.created_at, '%Y-%m') AS m, COUNT(*) AS n " + FROM_EVENTS
			+ " WHERE p.deleted_at IS NULL AND s.name = '작성됨' AND p.created_at >= NOW() - INTERVAL 12 MONTH GROUP BY m")
			.forEach(r -> monthly.put((String)r.get("m"), ((Number)r.get("n")).longValue()));
		o.put("monthlyNew", monthly);
		o.put("report", latestReport());
		return o;
	}

	public Map<String, Object> latestReport() {
		List<Map<String, Object>> rows = jdbc.queryForList(
			"SELECT week_start AS weekStart, headline, body, highlights, model, created_at AS createdAt FROM insight_reports ORDER BY week_start DESC LIMIT 1");
		return rows.isEmpty() ? null : rows.getFirst();
	}

	// ===================================================================== 브랜드

	public List<Map<String, Object>> brands(int limit) {
		return jdbc.queryForList("""
			SELECT b.name, COUNT(*) AS total, SUM(CASE WHEN %s THEN 1 ELSE 0 END) AS ongoing, MAX(p.created_at) AS lastPostedAt
			%s WHERE p.deleted_at IS NULL AND s.name = '작성됨' AND b.name IS NOT NULL
			GROUP BY b.name ORDER BY ongoing DESC, total DESC LIMIT ?
			""".formatted(ONGOING, FROM_EVENTS), Math.min(Math.max(limit, 1), 100));
	}

	public Map<String, Object> brand(String name) {
		Long brandId = jdbc.query("SELECT brand_id FROM brands WHERE name = ?", rs -> rs.next() ? rs.getLong(1) : null, name);
		if (brandId == null) {
			throw new NotFoundException("브랜드를 찾을 수 없어요.");
		}
		String base = FROM_EVENTS + " WHERE p.deleted_at IS NULL AND s.name = '작성됨' AND pi.brand_id = ? ";
		Map<String, Object> o = new LinkedHashMap<>();
		o.put("name", name);
		o.put("total", jdbc.queryForObject("SELECT COUNT(*) " + base, Long.class, brandId));
		o.put("ongoing", jdbc.queryForList("SELECT p.post_id AS postId, p.title, e.end_time AS endTime, e.end_type AS endType, pi.value_note AS valueNote, "
			+ THUMB + " AS imageUrl " + FROM_EVENTS + " WHERE " + ONGOING + " AND pi.brand_id = ? ORDER BY COALESCE(e.end_time, '9999-12-31'), p.post_id DESC LIMIT 30", brandId));
		o.put("past", jdbc.queryForList("SELECT p.post_id AS postId, p.title, e.start_time AS startTime, e.end_time AS endTime "
			+ base + " AND NOT (" + ONGOING + ") ORDER BY p.created_at DESC LIMIT 12", brandId));
		Map<String, Object> stats = jdbc.queryForMap("SELECT MIN(p.created_at) AS firstAt, MAX(p.created_at) AS lastAt, "
			+ "AVG(CASE WHEN e.start_time IS NOT NULL AND e.end_time IS NOT NULL THEN DATEDIFF(e.end_time, e.start_time) END) AS avgDays, "
			+ "COUNT(DISTINCT DATE_FORMAT(p.created_at, '%Y-%m')) AS activeMonths " + base, brandId);
		o.put("stats", stats);
		Map<String, Long> benefits = new LinkedHashMap<>();
		jdbc.queryForList("SELECT pi.benefit_types " + base + " AND pi.benefit_types IS NOT NULL", String.class, brandId)
			.forEach(t -> split(t).forEach(b -> benefits.merge(b, 1L, Long::sum)));
		o.put("byBenefit", benefits);
		return o;
	}

	static List<String> split(String csv) {
		return csv == null || csv.isBlank() ? List.of() : Arrays.stream(csv.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
	}

	static Timestamp ts(LocalDateTime t) {
		return t == null ? null : Timestamp.valueOf(t);
	}
}
