package com.eventorback.global.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String hostname;

	@Value("${spring.data.redis.password}")
	private String password;

	private LettuceConnectionFactory createLettuceConnectionFactory(int dbIndex) {
		RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
		config.setHostName(hostname);
		config.setPassword(password);
		config.setPort(6379);
		config.setDatabase(dbIndex);
		return new LettuceConnectionFactory(config);
	}

	private RedisTemplate<String, Object> createRedisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		GenericJackson2JsonRedisSerializer serializer =
			new GenericJackson2JsonRedisSerializer(objectMapper);

		template.setConnectionFactory(factory);
		template.setValueSerializer(serializer);
		template.setHashValueSerializer(serializer);
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashKeySerializer(new StringRedisSerializer());
		return template;
	}

	@Bean
	@Primary
	public RedisConnectionFactory redisConnectionFactory() {
		return createLettuceConnectionFactory(2);
	}

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		return createRedisTemplate(redisConnectionFactory);
	}

	@Bean("cacheRedisConnectionFactory")
	public RedisConnectionFactory cacheRedisConnectionFactory() {
		return createLettuceConnectionFactory(3);
	}

	@Bean("cacheRedisTemplate")
	public RedisTemplate<String, Object> cacheRedisTemplate(
		@Qualifier("cacheRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		return createRedisTemplate(redisConnectionFactory);
	}

	@Bean("keywordRedisConnectionFactory")
	public RedisConnectionFactory keywordRedisConnectionFactory() {
		return createLettuceConnectionFactory(4);
	}

	@Bean("keywordRedisTemplate")
	public RedisTemplate<String, Object> keywordRedisTemplate(
		@Qualifier("keywordRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		return createRedisTemplate(redisConnectionFactory);
	}

	@Bean("leaderRedisConnectionFactory")
	public RedisConnectionFactory leaderRedisConnectionFactory() {
		return createLettuceConnectionFactory(6);
	}

	@Bean("leaderRedisTemplate")
	public RedisTemplate<String, Object> leaderRedisTemplate(
		@Qualifier("leaderRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
		return createRedisTemplate(redisConnectionFactory);
	}

	@Bean("cacheManager")
	public RedisCacheManager redisCacheManager(
		@Qualifier("cacheRedisConnectionFactory") RedisConnectionFactory connectionFactory) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
			.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
			.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

		return RedisCacheManager.builder(connectionFactory)
			.cacheDefaults(config)
			.build();
	}
}

