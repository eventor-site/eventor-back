package com.eventorback.eureka.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * @author 이경헌
 * 애플리케이션의 상태를 나타내는 커스텀 Health Indicator입니다.
 */
@Component
@RequiredArgsConstructor
public class CustomHealthIndicator implements HealthIndicator {
	private final ApplicationStatus applicationStatus;

	/**
	 * 애플리케이션의 현재 상태를 기반으로 Health 상태를 결정합니다.
	 *
	 * @return 애플리케이션이 실행 중일 경우 "UP" 상태를, 중지된 경우 "DOWN" 상태를 반환합니다.
	 */
	@Override
	public Health health() {
		if (!applicationStatus.isStatus()) {
			return Health.down().build();
		}

		return Health.up().withDetail("service", "start").build();
	}
}
