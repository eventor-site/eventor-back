// package com.eventorback.global.config;
//
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.connection.RedisConnectionFactory;
// import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
// import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
// import org.springframework.data.redis.core.RedisTemplate;
// import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
// import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
// import org.springframework.data.redis.serializer.StringRedisSerializer;
//
// import lombok.RequiredArgsConstructor;
//
// @Configuration
// @RequiredArgsConstructor
// @EnableRedisRepositories(basePackages = "com.nhnacademy.bookstoreback.bookcache.repository", redisTemplateRef = "cacheRedisTemplate")
// public class CacheRedisConfig {
//
// 	@Bean("cacheRedisConnectionFactory")
// 	public RedisConnectionFactory cacheRedisConnectionFactory() {
// 		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
// 		redisStandaloneConfiguration.setHostName(keyManagerService.getSecret(redisProperty.getHost()));
// 		redisStandaloneConfiguration.setPort(Integer.parseInt(keyManagerService.getSecret(redisProperty.getPort())));
// 		redisStandaloneConfiguration.setPassword(keyManagerService.getSecret(redisProperty.getPassword()));
// 		redisStandaloneConfiguration.setDatabase(
// 			Integer.parseInt(keyManagerService.getSecret(redisProperty.getCartDatabase())));
// 		return new LettuceConnectionFactory(redisStandaloneConfiguration);
// 	}
//
// 	@Bean("cacheRedisTemplate")
// 	public RedisTemplate<String, Object> cacheRedisTemplate(
// 		@Qualifier("cacheRedisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
// 		RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();
// 		sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
// 		sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
// 		sessionRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
// 		sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
// 		sessionRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
// 		return sessionRedisTemplate;
// 	}
// }
