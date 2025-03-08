package com.eventorback.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.global.util.NumberUtil;
import com.eventorback.mail.service.impl.MailServiceImpl;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SendCodeRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserAttributeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.GetUserTokenInfo;
import com.eventorback.user.domain.dto.response.OauthDto;
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

	@AuthorizeRole("admin")
	@GetMapping
	public ApiResponse<Page<GetUserListResponse>> getUsers(@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(userService.getUsers(pageable));
	}

	@AuthorizeRole("admin")
	@GetMapping("/search")
	public ApiResponse<List<GetUserByIdentifier>> searchUserByIdentifier(@RequestParam String keyword) {
		return ApiResponse.createSuccess(userService.searchUserByIdentifier(keyword));
	}

	@AuthorizeRole("admin")
	@GetMapping("/search/userId")
	public ApiResponse<List<GetUserByUserId>> searchUserByUserId(@RequestParam Long userId) {
		return ApiResponse.createSuccess(userService.searchUserByUserId(userId));
	}

	/**
	 * 아이디를 통해 사용자의 토큰 정보를 조회합니다.
	 */
	@GetMapping("/info")
	public ApiResponse<GetUserTokenInfo> getUserInfoByIdentifier(@RequestParam String identifier) {
		return ApiResponse.createSuccess(userService.getUserTokenInfoByIdentifier(identifier));
	}

	@PostMapping("/oauth2/info")
	public ApiResponse<GetUserTokenInfo> getUserInfoByOauth(@RequestBody OauthDto request) {
		return ApiResponse.createSuccess(userService.getUserInfoByOauth(request));
	}

	@AuthorizeRole("admin")
	@GetMapping("/{userId}")
	public ApiResponse<GetUserResponse> getUser(@PathVariable Long userId) {
		return ApiResponse.createSuccess(userService.getUserInfo(userId));
	}

	@AuthorizeRole("admin")
	@PutMapping("/{userId}")
	public ApiResponse<Void> updateUserByAdmin(@PathVariable Long userId, @RequestBody UpdateUserRequest request) {
		userService.updateUser(userId, request);
		return ApiResponse.createSuccess();
	}

	@AuthorizeRole("admin")
	@PutMapping("/{userId}/attribute")
	public ApiResponse<Void> updateUserAttributeByAdmin(@PathVariable Long userId,
		@RequestBody UpdateUserAttributeRequest request) {
		userService.updateUserAttributeByAdmin(userId, request);
		return ApiResponse.createSuccess();
	}

	@AuthorizeRole("member")
	@GetMapping("/me")
	public ApiResponse<GetUserResponse> getUserInfo(@CurrentUserId Long userId) {
		return ApiResponse.createSuccess(userService.getUserInfo(userId));
	}

	@PutMapping("/me")
	public ApiResponse<Void> updateUser(@CurrentUserId Long userId, @RequestBody UpdateUserRequest request) {
		userService.updateUser(userId, request);
		return ApiResponse.createSuccess("회원 정보가 수정 되었습니다.");
	}

	@DeleteMapping("/me")
	ApiResponse<Void> withdrawUser(@CurrentUserId Long userId) {
		userService.withdrawUser(userId);
		return ApiResponse.createSuccess("회원 탈퇴 되었습니다.");
	}

	@GetMapping("/me/checkRoles")
	public ApiResponse<Boolean> meCheckRoles(@CurrentUser CurrentUserDto currentUser,
		@RequestParam String roleName) {
		return ApiResponse.createSuccess(userService.meCheckRoles(currentUser, roleName));
	}

	@GetMapping("/me/Roles")
	public ApiResponse<List<String>> meRoles(@CurrentUser CurrentUserDto currentUser) {
		return ApiResponse.createSuccess(userService.meRoles(currentUser));
	}

	@PostMapping("/me/checkNickname")
	public ApiResponse<Void> meCheckNickname(@CurrentUserId Long userId,
		@RequestBody CheckNicknameRequest request) {
		return ApiResponse.createSuccess(userService.meCheckNickname(userId, request));
	}

	@PutMapping("/me/lastLoginTime")
	public ApiResponse<Void> updateLastLoginTime(@RequestBody UpdateLastLoginTimeRequest request) {
		userService.updateLastLoginTime(request);
		return ApiResponse.createSuccess();
	}

	@PutMapping("/me/password")
	ApiResponse<Void> modifyPassword(@CurrentUserId Long userId, @RequestBody ModifyPasswordRequest request) {
		return ApiResponse.createSuccess(userService.modifyPassword(userId, request));
	}

	@PostMapping("/signup")
	public ApiResponse<Void> signup(@RequestBody SignUpRequest request) {
		userService.signup(request);
		return ApiResponse.createSuccess("회원 가입 되었습니다.");
	}

	@PostMapping("/signup/oauth2/exists")
	ApiResponse<Boolean> existsByOauth(@RequestBody OauthDto request) {
		return ApiResponse.createSuccess(userService.existsByOauth(request));
	}

	@PostMapping("/signup/checkIdentifier")
	ApiResponse<Void> checkIdentifier(@RequestBody CheckIdentifierRequest request) {
		return ApiResponse.createSuccess(userService.checkIdentifier(request));
	}

	@PostMapping("/signup/checkNickname")
	ApiResponse<Void> checkNickname(@RequestBody CheckNicknameRequest request) {
		return ApiResponse.createSuccess(userService.checkNickname(request));
	}

	@PostMapping("/signup/sendEmail")
	public ApiResponse<Void> sendEmail(@RequestBody SendCodeRequest request) {
		mailService.sendMail(request.email(), "회원가입", NumberUtil.createRandom(6));
		return ApiResponse.createSuccess(request.email() + "로 인증 번호를 전송했습니다.");
	}

	@GetMapping("/signup/checkEmail")
	public ApiResponse<Void> checkEmail(@RequestParam String email, @RequestParam String certifyCode) {
		String subject = "회원가입";
		boolean codeMatch = mailService.checkEmail(email, certifyCode, subject);
		if (codeMatch) {
			return ApiResponse.createSuccess("인증되었습니다.");
		}
		return ApiResponse.createSuccess("인증번호가 일치하지 않습니다.");
	}

	@PostMapping("/recover/identifier")
	ApiResponse<Void> recoverIdentifier(@RequestParam String email) {
		return ApiResponse.createSuccess(userService.recoverIdentifier(email));
	}

	@PostMapping("/recover/password")
	ApiResponse<Void> recoverPassword(@RequestParam String identifier) {
		return ApiResponse.createSuccess(userService.recoverPassword(identifier));
	}

}
