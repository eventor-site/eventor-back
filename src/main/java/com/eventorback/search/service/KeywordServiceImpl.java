package com.eventorback.search.service;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.eventorback.search.service.impl.KeywordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {
	private final RedisTemplate<String, Object> keywordRedisTemplate;

	public List<String> getTopKeywords() {
		return keywordRedisTemplate.opsForZSet()
			.reverseRange("search_keywords:score", 0, 9)
			.stream()
			.map(String::valueOf)
			.toList();
	}
}
