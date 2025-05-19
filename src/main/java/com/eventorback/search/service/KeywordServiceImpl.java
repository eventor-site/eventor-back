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
}
