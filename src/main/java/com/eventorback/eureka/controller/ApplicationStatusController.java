package com.eventorback.eureka.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.eureka.actuator.ApplicationStatus;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/back/actuator/status")
@RequiredArgsConstructor
public class ApplicationStatusController {
	private final ApplicationInfoManager applicationInfoManager;
	private final ApplicationStatus applicationStatus;

	@PostMapping
	@ResponseStatus(value = HttpStatus.OK)
	public void stopStatus() {
		applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.DOWN);
		applicationStatus.stopService();
		log.info("Application stopping");
	}

}