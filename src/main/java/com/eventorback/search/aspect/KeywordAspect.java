package com.eventorback.search.aspect;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class KeywordAspect {
	private final RedisTemplate<String, Object> keywordRedisTemplate;

	@AfterReturning("execution(* com.eventorback.search.service.ElasticSearchService.searchPosts(..))")
	public void recordKeyword(JoinPoint joinPoint) {

		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[1] instanceof String keyword) {
			if (keyword.isEmpty()) {
				return;
			}

			String timeKey = getCurrentTimeKey(); // 예: search_keywords:1425
			keywordRedisTemplate.opsForZSet().incrementScore(timeKey, keyword, 1);
			keywordRedisTemplate.expire(timeKey, Duration.ofHours(1));

			// 누적 키도 증가
			keywordRedisTemplate.opsForZSet().incrementScore("search_keywords:total", keyword, 1);
		}
	}

	private String getCurrentTimeKey() {
		LocalDateTime now = LocalDateTime.now();

		// 5분 단위로 라운딩
		int minuteBlock = (now.getMinute() / 5) * 5;
		now = now.withMinute(minuteBlock).withSecond(0).withNano(0);

		// HHmm 형식으로 변환 (예: 1425)
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
		return "search_keywords:" + now.format(formatter);
	}

}
