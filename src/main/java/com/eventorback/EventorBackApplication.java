package com.eventorback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

// @EnableDiscoveryClient
// @EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableFeignClients
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
public class EventorBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventorBackApplication.class, args);
	}

}
