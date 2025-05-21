package com.eventorback.global.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LeaderElectionManager {

	private final RedisTemplate<String, Object> leaderRedisTemplate;
	private static final String LOCK_KEY = "backend:leader";
	private static final Duration LOCK_TTL = Duration.ofSeconds(60); // 락 유지 시간 (스케줄 간격보다 약간 짧게)

	@Value("${spring.application.name}")
	private String appName;

	@Value("${server.port}")
	private String port;

	/**
	 * Redis 락 획득 → 리더 여부 판단
	 */
	public boolean tryAcquireLeadership() {
		String instanceId = appName + "-" + port;

		Boolean isAcquired = leaderRedisTemplate.opsForValue().setIfAbsent(
			LOCK_KEY, instanceId, LOCK_TTL
		);

		if (Boolean.TRUE.equals(isAcquired)) {
			// 락 선점 성공 → 리더
			return true;
		}

		// 이미 락을 보유하고 있는 인스턴스인지 확인
		String currentLeader = (String)leaderRedisTemplate.opsForValue().get(LOCK_KEY);
		return instanceId.equals(currentLeader);
	}
}
