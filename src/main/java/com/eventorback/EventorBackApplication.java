package com.eventorback;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@EnableCaching
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10m")
@SpringBootApplication
@ConfigurationPropertiesScan
public class EventorBackApplication {

	/** DB의 날짜 값은 한국 시간으로 저장돼 있다. 실행 환경(도커 UTC 등)과 관계없이 같은 시간대로 동작하게 고정한다 */
	public static final String TIME_ZONE = "Asia/Seoul";

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE));
		SpringApplication.run(EventorBackApplication.class, args);
	}
}
