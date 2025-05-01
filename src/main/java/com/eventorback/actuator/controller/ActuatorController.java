package com.eventorback.actuator.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/actuator")
public class ActuatorController {

	@Value("${server.port}")
	private int port;

	@GetMapping("/health")
	public ResponseEntity<ApiResponse<Boolean>> backCheckHealth() {
		return ApiResponse.createSuccess(true);
	}

	@GetMapping("/version")
	public ResponseEntity<ApiResponse<String>> backCheckVersion() {
		String version;
		if (port == 8101 || port == 8102) {
			version = "blue";
		} else {
			version = "green";
		}
		return ApiResponse.createSuccess(version, null);
	}

}
