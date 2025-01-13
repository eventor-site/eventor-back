package com.eventorback.userstop.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.userstop.domain.dto.UserStopDto;
import com.eventorback.userstop.domain.dto.response.GetUserStopResponse;
import com.eventorback.userstop.service.UserStopService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/userStops")
public class UserStopController {
	private final UserStopService userStopService;

	@GetMapping
	public ResponseEntity<List<GetUserStopResponse>> getUserStops() {
		return ResponseEntity.status(HttpStatus.OK).body(userStopService.getUserStops());
	}

	@GetMapping("/{userStopId}")
	public ResponseEntity<UserStopDto> getUserStop(@PathVariable Long userStopId) {
		return ResponseEntity.status(HttpStatus.OK).body(userStopService.getUserStop(userStopId));
	}

	@PostMapping
	public ResponseEntity<Void> createUserStop(
		@RequestBody UserStopDto request) {
		userStopService.createUserStop(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@DeleteMapping("/{userStopId}")
	public ResponseEntity<Void> deleteUserStop(@PathVariable Long userStopId) {
		userStopService.deleteUserStop(userStopId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
