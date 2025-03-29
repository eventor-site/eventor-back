package com.eventorback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableCaching
@EnableScheduling
@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class,
	ReactiveElasticsearchRepositoriesAutoConfiguration.class})
@EnableFeignClients
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
public class EventorBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventorBackApplication.class, args);
	}

}
