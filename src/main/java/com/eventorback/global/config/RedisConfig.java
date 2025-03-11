package com.eventorback.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String hostname;

	@Value("${spring.data.redis.password}")
	private String password;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setPort(6379);
		redisStandaloneConfiguration.setHostName(hostname);
		redisStandaloneConfiguration.setPassword(password);
		redisStandaloneConfiguration.setDatabase(2);
		return new LettuceConnectionFactory(redisStandaloneConfiguration);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();
		sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
		sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
		sessionRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
		sessionRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
		return sessionRedisTemplate;
	}
}

