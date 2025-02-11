package com.eventorback.global.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl("jdbc:mysql://localhost:3306/eventor");
		dataSource.setUsername("root");
		dataSource.setPassword("1q2w3e4r!");
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

		// 최적화 파라미터 설정
		dataSource.setInitialSize(100);
		dataSource.setMaxTotal(100);
		dataSource.setMaxIdle(100);
		dataSource.setMinIdle(100);

		// 추가 최적화 설정
		dataSource.setTestOnBorrow(true);
		dataSource.setValidationQuery("SELECT 1");

		return dataSource;
	}
}