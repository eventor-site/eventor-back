package com.eventorback.userstop.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.eventorback.global.dto.ApiResponse;
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
	public ResponseEntity<ApiResponse<Page<GetUserStopResponse>>> getUserStops(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(userStopService.getUserStops(pageable));
	}

	@AuthorizeRole("admin")
	@GetMapping("/{userStopId}")
	public ResponseEntity<ApiResponse<UserStopDto>> getUserStop(@PathVariable Long userStopId) {
		return ApiResponse.createSuccess(userStopService.getUserStop(userStopId));
	}

	@AuthorizeRole("admin")
	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<GetUserStopByUserIdResponse>>> getUserStopsByUserId(
		@RequestParam Long userId) {
		return ApiResponse.createSuccess(userStopService.getUserStopsByUserId(userId));
	}

	@AuthorizeRole("admin")
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createUserStop(
		@RequestBody UserStopDto request) {
		userStopService.createUserStop(request);
		return ApiResponse.createSuccess("회원 활동을 정지 시켰습니다.");
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/{userStopId}")
	public ResponseEntity<ApiResponse<Void>> deleteUserStop(@PathVariable Long userStopId) {
		userStopService.deleteUserStop(userStopId);
		return ApiResponse.createSuccess("회원 활동 정지를 취소 하였습니다.");
	}
}
