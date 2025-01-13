package com.eventorback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

// @EnableDiscoveryClient
// @EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableWebSecurity
@EnableFeignClients
@EnableAspectJAutoProxy
@ConfigurationPropertiesScan
public class EventorBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventorBackApplication.class, args);
	}

}
