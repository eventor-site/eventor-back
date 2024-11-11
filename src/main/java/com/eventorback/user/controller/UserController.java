package com.eventorback.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sikyeojoback.user.domain.dto.request.SignUpRequest;
import com.sikyeojoback.user.domain.dto.response.GetUserByAddShopResponse;
import com.sikyeojoback.user.domain.dto.response.UserTokenInfo;
import com.sikyeojoback.user.service.UserService;

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
	 * 이메일을 통해 사용자의 토큰 정보를 조회합니다.
	 *
	 * @param id 사용자 아이디를 헤더로 전달합니다.
	 * @return 사용자 정보가 포함된 {@link ResponseEntity} 객체를 반환합니다.
	 */
	@GetMapping("/info")
	public ResponseEntity<UserTokenInfo> getUserInfoById(@RequestHeader("X-User-userId") String id) {
		UserTokenInfo user = userService.getUserTokenInfoById(id);

		if (user == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	@GetMapping("/search")
	public ResponseEntity<List<GetUserByAddShopResponse>> searchUserById(@RequestParam String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.searchUserById(keyword));
	}

}
