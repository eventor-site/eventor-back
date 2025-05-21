package com.eventorback.search.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.eventorback.global.annotation.TimedExecution;
import com.eventorback.search.service.impl.KeywordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {
	private final RedisTemplate<String, Object> keywordRedisTemplate;
	private static final long UNUSED_DAYS_THRESHOLD = 1; // 1일 동안 검색 안 된 키워드 제거

	@Override
	public List<String> getKeywords() {
		return keywordRedisTemplate.opsForZSet()
			.reverseRange("search_keywords:score", 0, 99)
			.stream()
			.map(String::valueOf)
			.toList();
	}

	@Override
	public void deleteKeyword(String keyword) {
		keywordRedisTemplate.opsForZSet().remove("search_keywords:total", keyword);
		keywordRedisTemplate.opsForZSet().remove("search_keywords:score", keyword);
		keywordRedisTemplate.opsForHash().delete("search_keywords:last_used", keyword);
	}

	@Override
	@TimedExecution("인기 검색어 제거")
	public void cleanUpOldTopKeywords() {
		Set<Object> keywords = keywordRedisTemplate.opsForZSet().range("search_keywords:total", 0, -1);

		if (keywords == null) {
			return;
		}

		LocalDateTime now = LocalDateTime.now();
		for (Object obj : keywords) {
			String keyword = String.valueOf(obj);
			String lastUsedStr = (String)keywordRedisTemplate.opsForHash()
				.get("search_keywords:last_used", keyword);

			if (lastUsedStr == null) {
				continue;
			}

			LocalDateTime lastUsed = LocalDateTime.parse(lastUsedStr);

			if (Duration.between(lastUsed, now).toDays() >= UNUSED_DAYS_THRESHOLD) {
				keywordRedisTemplate.opsForZSet().remove("search_keywords:total", keyword);
				keywordRedisTemplate.opsForZSet().remove("search_keywords:score", keyword);
				keywordRedisTemplate.opsForHash().delete("search_keywords:last_used", keyword);
			}
		}
	}

	@Override
	public void updateTopKeywords() {
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

			keywordRedisTemplate.opsForZSet().add("search_keywords:score", keyword, score);
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
