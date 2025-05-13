package com.eventorback.actuator.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/actuator")
public class ActuatorController {

	@Value("${server.port}")
	private int port;

	@GetMapping("/health")
	public ResponseEntity<Boolean> backCheckHealth() {
		return ResponseEntity.ok(true);
	}

	@GetMapping("/version")
	public ResponseEntity<String> backCheckVersion() {
		String version;
		if (port == 8101 || port == 8102) {
			version = "blue";
		} else {
			version = "green";
		}
		return ResponseEntity.ok(version);
	}

}
