package com.eventorback.statistic.aspect;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.statistic.domain.entity.Statistic;
import com.eventorback.statistic.repository.StatisticRepository;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class StatisticAspect {
	private final StatisticRepository statisticRepository;
	private final RedisTemplate<String, Object> cacheRedisTemplate;

	@AfterReturning("execution(* com.eventorback.post.controller.PostController.getHotEventPosts(..))")
	public void collectInfoAfterReturningGetMainPage() {
		Statistic statistic = statisticRepository.findByDate(LocalDate.now()).orElse(null);

		if (statistic == null) {
			statistic = new Statistic();
		}

		statistic.increaseVisitedCount();
		statisticRepository.save(statistic);
	}

	@AfterReturning("execution(* com.eventorback.user.controller.UserController.signup(..))")
	public void collectInfoAfterReturningSignup() {
		Statistic statistic = statisticRepository.findByDate(LocalDate.now()).orElse(null);

		if (statistic == null) {
			statistic = new Statistic();
		}

		statistic.increaseSignupCount();
		statisticRepository.save(statistic);
	}

	@AfterReturning("execution(* com.eventorback.user.service.impl.UserServiceImpl.updateLoginAt(..))")
	public void collectInfoAfterReturningLoginMemberCount(JoinPoint joinPoint) throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();

		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof UpdateLoginAtRequest request) {

			// Redis 에서 데이터 조회 (JSON 으로 변환된 값)
			String userIdsJson = (String)cacheRedisTemplate.opsForValue().get("userIds");

			Set<Long> userIds;
			if (userIdsJson == null) {
				userIds = new HashSet<>();
			} else {
				userIds = objectMapper.readValue(userIdsJson, new TypeReference<Set<Long>>() {
				});
			}

			userIds.add(request.userId());

			// 현재 시간과 자정까지 남은 시간 계산
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime midnight = now.toLocalDate().atTime(LocalTime.MAX);
			long ttlInSeconds = Duration.between(now, midnight).getSeconds();

			// JSON 형태로 변환하여 Redis에 저장
			String updatedUserIdsJson = objectMapper.writeValueAsString(userIds);
			cacheRedisTemplate.opsForValue().set("userIds", updatedUserIdsJson, ttlInSeconds, TimeUnit.SECONDS);

			// 방문자 수 통계 업데이트
			Statistic statistic = statisticRepository.findByDate(LocalDate.now()).orElse(null);
			if (statistic == null) {
				statistic = new Statistic();
			}

			statistic.updateLoginCount((long)userIds.size());
			statisticRepository.save(statistic);
		}
	}
}

