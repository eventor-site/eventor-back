package com.eventorback.global.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.url}")
	private String url;

	/**
	 * PoolSize = Tn × ( Cm - 1 ) + ( Tn / 2 )
	 *
	 * thread count : 12
	 * simultaneous connection count : 2
	 * pool size : 12 * ( 2 – 1 ) + (12 / 2) = 18
	 */
	@Bean
	public BasicDataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername("root");
		dataSource.setPassword("1q2w3e4r!");
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

		// 최적화 파라미터 설정
		dataSource.setInitialSize(18);
		dataSource.setMaxTotal(18);
		dataSource.setMaxIdle(18);
		dataSource.setMinIdle(18);

		// 추가 최적화 설정
		dataSource.setTestOnBorrow(true);
		dataSource.setValidationQuery("SELECT 1");

		return dataSource;
	}
}