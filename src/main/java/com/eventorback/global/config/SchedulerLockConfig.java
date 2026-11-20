package com.eventorback.global.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;

/**
 * 스케줄 작업을 여러 인스턴스 중 하나만 실행하게 하는 DB 락(shedlock 테이블, V2 마이그레이션).
 */
@Configuration
public class SchedulerLockConfig {

	@Bean
	public LockProvider lockProvider(DataSource dataSource) {
		return new JdbcTemplateLockProvider(JdbcTemplateLockProvider.Configuration.builder()
			.withJdbcTemplate(new JdbcTemplate(dataSource))
			.usingDbTime()
			.build());
	}
}
