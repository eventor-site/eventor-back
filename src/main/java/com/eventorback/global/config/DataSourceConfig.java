package com.eventorback.global.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class DataSourceConfig {

	@Value("${spring.datasource.dbcp2.username}")
	private String username;

	@Value("${spring.datasource.dbcp2.password}")
	private String password;

	/**
	 * PoolSize = Tn × ( Cm - 1 ) + ( Tn / 2 )
	 * thread count : 12
	 * simultaneous connection count : 2
	 * pool size : 12 * ( 2 – 1 ) + (12 / 2) = 18
	 */

	@Bean
	@Profile("dev")
	public DataSource devDataSource(@Value("${spring.datasource.url}") String url) {
		return createDataSource(url);
	}

	@Bean
	@Profile("prod")
	public DataSource routingDataSource(DataSource writeDataSource, DataSource readDataSource) {
		RoutingDataSource routingDataSource = new RoutingDataSource();
		Map<Object, Object> dataSourceMap = new HashMap<>();
		dataSourceMap.put(DataSourceContextHolder.WRITE, writeDataSource);
		dataSourceMap.put(DataSourceContextHolder.READ, readDataSource);

		routingDataSource.setTargetDataSources(dataSourceMap);
		routingDataSource.setDefaultTargetDataSource(writeDataSource);

		return routingDataSource;
	}

	@Bean
	@Profile("prod")
	public DataSource writeDataSource(@Value("${spring.datasource.router.url.rw}") String url) {
		return createDataSource(url);
	}

	@Bean
	@Profile("prod")
	public DataSource readDataSource(@Value("${spring.datasource.router.url.ro}") String url) {
		return createDataSource(url);
	}

	private BasicDataSource createDataSource(String url) {
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

		// 💡 기존 설정 그대로 반영
		dataSource.setInitialSize(18);
		dataSource.setMaxTotal(18);
		dataSource.setMaxIdle(18);
		dataSource.setMinIdle(18);

		dataSource.setTestOnBorrow(true);
		dataSource.setValidationQuery("SELECT 1");

		return dataSource;
	}
}