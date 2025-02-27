package com.eventorback.userstop.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.userstop.domain.dto.UserStopDto;
import com.eventorback.userstop.domain.dto.response.GetUserStopByUserIdResponse;
import com.eventorback.userstop.domain.dto.response.GetUserStopResponse;
import com.eventorback.userstop.service.UserStopService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/userStops")
public class UserStopController {
	private final UserStopService userStopService;

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<Page<GetUserStopResponse>> getUserStops(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(userStopService.getUserStops(pageable));
	}

	@AuthorizeRole("admin")
	@GetMapping("/{userStopId}")
	public ResponseEntity<UserStopDto> getUserStop(@PathVariable Long userStopId) {
		return ResponseEntity.status(HttpStatus.OK).body(userStopService.getUserStop(userStopId));
	}

	@AuthorizeRole("admin")
	@GetMapping("/users")
	public ResponseEntity<List<GetUserStopByUserIdResponse>> getUserStopsByUserId(@RequestParam Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(userStopService.getUserStopsByUserId(userId));
	}

	@AuthorizeRole("admin")
	@PostMapping
	public ResponseEntity<Void> createUserStop(
		@RequestBody UserStopDto request) {
		userStopService.createUserStop(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/{userStopId}")
	public ResponseEntity<Void> deleteUserStop(@PathVariable Long userStopId) {
		userStopService.deleteUserStop(userStopId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
