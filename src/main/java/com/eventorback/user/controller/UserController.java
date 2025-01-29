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

import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.global.util.NumberUtil;
import com.eventorback.mail.service.impl.MailServiceImpl;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.OauthDto;
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
	public ResponseEntity<List<GetUserByIdentifier>> searchUserByIdentifier(@RequestParam String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.searchUserByIdentifier(keyword));
	}

	/**
	 * 아이디를 통해 사용자의 토큰 정보를 조회합니다.
	 */
	@GetMapping("/info")
	public ResponseEntity<UserTokenInfo> getUserInfoByIdentifier(@RequestParam String identifier) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserTokenInfoByIdentifier(identifier));
	}

	@PostMapping("/oauth2/info")
	public ResponseEntity<UserTokenInfo> getUserInfoByOauth(@RequestBody OauthDto request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.getUserInfoByOauth(request));
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

	@DeleteMapping("/me")
	ResponseEntity<Void> withdrawUser(@CurrentUserId Long userId) {
		userService.withdrawUser(userId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/me/checkRoles")
	public ResponseEntity<Boolean> meCheckRoles(@CurrentUser CurrentUserDto currentUser) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.meCheckRoles(currentUser));
	}

	@PostMapping("/me/checkNickname")
	public ResponseEntity<String> meCheckNickname(@CurrentUserId Long userId,
		@RequestBody CheckNicknameRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.meCheckNickname(userId, request));
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

	@PostMapping("/signup")
	public ResponseEntity<Void> signup(@RequestBody SignUpRequest request) {
		userService.signup(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PostMapping("/signup/oauth2/exists")
	ResponseEntity<Boolean> existsByOauth(@RequestBody OauthDto request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.existsByOauth(request));
	}

	@PostMapping("/signup/checkIdentifier")
	ResponseEntity<String> checkIdentifier(@RequestBody CheckIdentifierRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.checkIdentifier(request));
	}

	@PostMapping("/signup/checkNickname")
	ResponseEntity<String> checkNickname(@RequestBody CheckNicknameRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.checkNickname(request));
	}

	@PostMapping("/signup/sendEmail")
	public ResponseEntity<String> sendEmail(@RequestBody CheckIdentifierRequest request) {
		String subject = "회원가입";
		boolean isEmailExist = userService.existsByIdentifier(request);
		if (!isEmailExist) {
			mailService.sendMail(request.identifier(), subject, NumberUtil.createRandom(6));
			return ResponseEntity.status(HttpStatus.OK).body(request.identifier() + "로 인증 번호를 전송했습니다.");
		}
		return ResponseEntity.status(HttpStatus.OK).body("이미 가입된 아이디 입니다.");
	}

	@GetMapping("/signup/checkEmail")
	public ResponseEntity<String> checkEmail(@RequestParam String email, @RequestParam String certifyCode) {
		String subject = "회원가입";
		boolean codeMatch = mailService.checkEmail(email, certifyCode, subject);
		if (codeMatch) {
			return ResponseEntity.status(HttpStatus.OK).body("인증되었습니다.");
		}
		return ResponseEntity.status(HttpStatus.OK).body("인증번호가 일치하지 않습니다.");
	}

	@PostMapping("/recover/identifier")
	ResponseEntity<String> recoverIdentifier(@RequestParam String email) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.recoverIdentifier(email));
	}

	@PostMapping("/recover/password")
	ResponseEntity<String> recoverPassword(@RequestParam String identifier) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.recoverPassword(identifier));
	}

}
