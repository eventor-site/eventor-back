package com.eventorback.global.config;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager cacheManager(
		@Qualifier("cacheRedisTemplate") RedisTemplate<String, Object> cacheRedisTemplate) {
		RedisCacheManager.RedisCacheManagerBuilder builder = RedisCacheManager.builder(
			Objects.requireNonNull(cacheRedisTemplate.getConnectionFactory()));
		return builder.build();
	}

}