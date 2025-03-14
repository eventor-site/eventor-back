package com.eventorback.eureka.actuator;

import org.springframework.stereotype.Component;

import lombok.Getter;

/**
 * @author 이경헌
 * 애플리케이션의 상태를 나타내는 컴포넌트입니다.
 */
@Getter
@Component
public class ApplicationStatus {
	private boolean status = true;

	/**
	 * 애플리케이션의 상태를 중지된 상태로 변경합니다.
	 */
	public void stopService() {
		this.status = false;

	}
}
