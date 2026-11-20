package com.eventorback.search.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.global.annotation.TimedExecution;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * 인기 검색어(V2 마이그레이션의 search_keywords, search_keyword_hits).
 * <p>
 * 점수 = (직전 5분 검색 수 - 그 전 5분 검색 수) × 2.0 + ln(누적 검색 수 + 1) × 1.2  — 이전 Redis 구현과 같은 식.
 * 급상승(직전 구간 증가분)을 크게, 누적 인기도는 로그로 완만하게 반영한다.
 * 하루 동안 검색되지 않은 키워드는 지운다.
 */
@Service
@RequiredArgsConstructor
public class KeywordService {
	private static final int BUCKET_MINUTES = 5;
	private static final int MAX_KEYWORD_LENGTH = 100;

	private final JdbcTemplate jdbcTemplate;

	/** 검색할 때마다 호출. 현재 5분 구간 수와 누적 수를 1씩 올린다 */
	@Transactional
	public void record(String rawKeyword) {
		if (rawKeyword == null) {
			return;
		}
		String keyword = rawKeyword.trim().replaceAll("\\s+", " ");
		if (keyword.isEmpty() || keyword.length() > MAX_KEYWORD_LENGTH) {
			return;
		}
		LocalDateTime now = LocalDateTime.now();
		jdbcTemplate.update("""
			INSERT INTO search_keyword_hits (keyword, bucket_time, hit_count) VALUES (?, ?, 1)
			ON DUPLICATE KEY UPDATE hit_count = hit_count + 1""", keyword, Timestamp.valueOf(bucket(now, 0)));
		jdbcTemplate.update("""
			INSERT INTO search_keywords (keyword, total_count, score, last_searched_at) VALUES (?, 1, 0, ?)
			ON DUPLICATE KEY UPDATE total_count = total_count + 1, last_searched_at = VALUES(last_searched_at)""",
			keyword, Timestamp.valueOf(now));
	}

	/** 점수 순 상위 키워드 */
	@Transactional(readOnly = true)
	public List<String> getKeywords(int limit) {
		return jdbcTemplate.queryForList(
			"SELECT keyword FROM search_keywords ORDER BY score DESC, total_count DESC LIMIT ?", String.class,
			Math.max(1, Math.min(limit, 100)));
	}

	@Transactional
	public void deleteKeyword(String keyword) {
		jdbcTemplate.update("DELETE FROM search_keyword_hits WHERE keyword = ?", keyword);
		jdbcTemplate.update("DELETE FROM search_keywords WHERE keyword = ?", keyword);
	}

	/** 5분마다 점수 다시 계산(직전 구간이 막 끝난 시점) */
	@Transactional
	@Scheduled(cron = "5 */5 * * * *")
	@SchedulerLock(name = "keyword.updateScores", lockAtMostFor = "4m")
	public void updateTopKeywords() {
		LocalDateTime now = LocalDateTime.now();
		Timestamp current = Timestamp.valueOf(bucket(now, -1));
		Timestamp previous = Timestamp.valueOf(bucket(now, -2));
		jdbcTemplate.update("""
			UPDATE search_keywords k
			LEFT JOIN search_keyword_hits c ON c.keyword = k.keyword AND c.bucket_time = ?
			LEFT JOIN search_keyword_hits p ON p.keyword = k.keyword AND p.bucket_time = ?
			SET k.score = (COALESCE(c.hit_count, 0) - COALESCE(p.hit_count, 0)) * 2.0 + LN(k.total_count + 1) * 1.2""",
			current, previous);
	}

	/** 매일 00:00 하루 넘게 검색되지 않은 키워드와 1시간 지난 구간 기록 정리 */
	@Transactional
	@Scheduled(cron = "0 0 0 * * *")
	@SchedulerLock(name = "keyword.cleanup", lockAtMostFor = "10m")
	@TimedExecution("인기 검색어 정리")
	public void cleanUpOldTopKeywords() {
		LocalDateTime now = LocalDateTime.now();
		jdbcTemplate.update("DELETE FROM search_keywords WHERE last_searched_at < ?", Timestamp.valueOf(now.minusDays(1)));
		jdbcTemplate.update("DELETE FROM search_keyword_hits WHERE bucket_time < ?", Timestamp.valueOf(now.minusHours(1)));
	}

	/** offset 구간 전(-1: 직전 5분 구간)의 시작 시각 */
	static LocalDateTime bucket(LocalDateTime time, int offset) {
		LocalDateTime start = time.withMinute(time.getMinute() / BUCKET_MINUTES * BUCKET_MINUTES).withSecond(0).withNano(0);
		return start.plusMinutes((long)offset * BUCKET_MINUTES);
	}
}
