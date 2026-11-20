package com.eventorback.global.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 로컬 메모리 캐시(Caffeine, Redis 대체).
 * 캐시는 백엔드 인스턴스마다 따로 있으므로, 한 인스턴스에서 지운 캐시가 다른 인스턴스에는 TTL까지 남을 수 있다.
 * 그래서 TTL을 짧게 둔다.
 * <ul>
 *     <li>cache: 메인 목록·등급·금지 닉네임 등 10분</li>
 *     <li>categoryIds, categoryNames: 카테고리 10분</li>
 *     <li>festivals: 관광공사 축제 정보 6시간(외부 API 일일 호출 한도 보호)</li>
 * </ul>
 */
@Configuration
public class CacheConfig {

	@Bean
	public CacheManager cacheManager() {
		CaffeineCacheManager manager = new CaffeineCacheManager();
		manager.setAllowNullValues(false);
		manager.registerCustomCache("cache", caffeine(Duration.ofMinutes(10), 1_000));
		manager.registerCustomCache("categoryIds", caffeine(Duration.ofMinutes(10), 500));
		manager.registerCustomCache("categoryNames", caffeine(Duration.ofMinutes(10), 500));
		manager.registerCustomCache("festivals", caffeine(Duration.ofHours(6), 100));
		// 위에 없는 이름은 만들지 않는다(오타로 새 캐시가 생기는 것 방지)
		manager.setCacheNames(java.util.List.of());
		return manager;
	}

	private static com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeine(Duration ttl, long maxSize) {
		return Caffeine.newBuilder().expireAfterWrite(ttl).maximumSize(maxSize).recordStats().build();
	}
}
