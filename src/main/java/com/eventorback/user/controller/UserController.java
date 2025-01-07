package com.eventorback.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.UserTokenInfo;
import com.eventorback.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/back/users")
@RequiredArgsConstructor

public class UserController {
	private final UserService userService;

	@PostMapping("/sign-up")
	public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
		userService.signUp(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 아이디를 통해 사용자의 토큰 정보를 조회합니다.
	 */
	@GetMapping("/info")
	public ResponseEntity<UserTokenInfo> getUserInfoByIdentifier(@RequestParam String identifier) {
		UserTokenInfo user = userService.getUserTokenInfoByIdentifier(identifier);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@GetMapping("/search")
	public ResponseEntity<List<GetUserByAddShopResponse>> searchUserById(@RequestParam String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.searchUserById(keyword));
	}

	@GetMapping("/me")
	public ResponseEntity<GetUserResponse> getUserInfo(@CurrentUserId Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(userId));
	}

	@PutMapping("/me")
	public ResponseEntity<Void> updateUser(@CurrentUserId Long userId, @RequestBody UpdateUserRequest request) {
		userService.updateUser(userId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/me/lastLoginTime")
	public ResponseEntity<Void> updateLastLoginTime(@CurrentUserId Long userId,
		@RequestBody UpdateLastLoginTimeRequest request) {
		userService.updateLastLoginTime(userId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
