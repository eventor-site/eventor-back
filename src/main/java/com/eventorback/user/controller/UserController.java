package com.eventorback.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.mail.service.impl.MailServiceImpl;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
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
	private final MailServiceImpl mailService;

	@GetMapping("/search")
	public ResponseEntity<List<GetUserByAddShopResponse>> searchUserById(@RequestParam String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.searchUserById(keyword));
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

	@GetMapping("/me")
	public ResponseEntity<GetUserResponse> getUserInfo(@CurrentUserId Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfo(userId));
	}

	@PostMapping("/signUp/checkIdentifier")
	ResponseEntity<String> checkIdentifier(@RequestBody CheckIdentifierRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.checkIdentifier(request));
	}

	@PostMapping("/signUp")
	public ResponseEntity<Void> signUp(@RequestBody SignUpRequest request) {
		userService.signUp(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/me")
	public ResponseEntity<Void> updateUser(@CurrentUserId Long userId, @RequestBody UpdateUserRequest request) {
		userService.updateUser(userId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/me/lastLoginTime")
	public ResponseEntity<Void> updateLastLoginTime(@RequestBody UpdateLastLoginTimeRequest request) {
		userService.updateLastLoginTime(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/me/password")
	ResponseEntity<String> modifyPassword(@CurrentUserId Long userId, @RequestBody ModifyPasswordRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.modifyPassword(userId, request));
	}

	@DeleteMapping("/me")
	ResponseEntity<Void> withdrawUser(@CurrentUserId Long userId) {
		userService.withdrawUser(userId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PostMapping("/signUp/sendEmail")
	public ResponseEntity<String> sendEmail(@RequestParam String email) {
		String subject = "회원가입";
		boolean isEmailExist = userService.existsByEmail(email);
		if (!isEmailExist) {
			mailService.sendMail(email, subject);
			return ResponseEntity.status(HttpStatus.OK).body(email + "로 인증 번호를 전송했습니다.");
		}
		return ResponseEntity.status(HttpStatus.OK).body("이미 사용 중인 이메일입니다.");
	}

	@GetMapping("/signUp/checkEmail")
	public ResponseEntity<String> checkEmail(@RequestParam String email, @RequestParam String certifyCode) {
		String subject = "회원가입";
		boolean codeMatch = mailService.checkEmail(email, certifyCode, subject);
		if (codeMatch) {
			return ResponseEntity.status(HttpStatus.OK).body("인증되었습니다.");
		}
		return ResponseEntity.status(HttpStatus.OK).body("인증번호가 일치하지 않습니다.");
	}

}
