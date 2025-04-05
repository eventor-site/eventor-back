package com.eventorback.statistic.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.eventorback.statistic.domain.entity.Statistic;
import com.eventorback.statistic.repository.StatisticRepository;
import com.eventorback.statistic.service.StatisticService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {
	private final StatisticRepository statisticRepository;
	private final RedisTemplate<String, Object> cacheRedisTemplate;

	@SneakyThrows
	@Override
	public void saveVisitor(String uuid) {
		ObjectMapper objectMapper = new ObjectMapper();

		// Redis 에서 데이터 조회 (JSON 으로 변환된 값)
		String visitorsJson = (String)cacheRedisTemplate.opsForValue().get("visitors");

		Set<String> visitors;
		if (visitorsJson == null) {
			visitors = new HashSet<>();
		} else {
			visitors = objectMapper.readValue(visitorsJson, new TypeReference<Set<String>>() {
			});
		}

		visitors.add(uuid);

		// 현재 시간과 자정까지 남은 시간 계산
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime midnight = now.toLocalDate().atTime(LocalTime.MAX);
		long ttlInSeconds = Duration.between(now, midnight).getSeconds();

		// JSON 형태로 변환하여 Redis에 저장
		String updatedUserIdsJson = objectMapper.writeValueAsString(visitors);
		cacheRedisTemplate.opsForValue().set("visitors", updatedUserIdsJson, ttlInSeconds, TimeUnit.SECONDS);

		// 방문자 수 통계 업데이트
		Statistic statistic = statisticRepository.findByDate(LocalDate.now()).orElse(null);
		if (statistic == null) {
			statistic = new Statistic();
		}

		statistic.updateVisitorCount((long)visitors.size());
		statisticRepository.save(statistic);

	}
}
