package com.eventorback.search.scheduler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RankKeywordScheduler {
	private final RedisTemplate<String, Object> keywordRedisTemplate;

	@Scheduled(cron = "0 */5 * * * *") // 매 5분마다
	public void computeTrendingKeywords() {
		String currentKey = getTimeKey(-1);   // 직전 5분 구간
		String previousKey = getTimeKey(-2); // 직전전 5분 구간

		Set<Object> keywords = keywordRedisTemplate.opsForZSet()
			.union(currentKey, previousKey); // 두 키에 있는 모든 키워드 가져오기

		if (keywords == null) {
			return;
		}

		for (Object object : keywords) {
			String keyword = String.valueOf(object);
			double current = getScore(currentKey, keyword);
			double previous = getScore(previousKey, keyword);
			double total = getScore("search_keywords:total", keyword);

			double delta = current - previous;
			double score = delta * 2.0 + Math.log(total + 1) * 1.2;

			keywordRedisTemplate.opsForZSet().add("search_scores:computed", keyword, score);
		}

	}

	private String getTimeKey(int offset) {
		// 현재 시각
		LocalDateTime now = LocalDateTime.now();

		// 5분 단위 정규화
		int minuteBlock = (now.getMinute() / 5) * 5;
		now = now.withMinute(minuteBlock).withSecond(0).withNano(0);

		// offset 적용 (예: -1 → 이전 블록, +1 → 다음 블록)
		now = now.plusMinutes(offset * 5L);

		// 키 포맷: search_keywords:HHmm (예: search_keywords:1415)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
		return "search_keywords:" + now.format(formatter);
	}

	private double getScore(String key, String keyword) {
		Double score = keywordRedisTemplate.opsForZSet().score(key, keyword);
		return score != null ? score : 0.0;
	}
}
