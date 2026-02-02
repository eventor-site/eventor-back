package com.eventorback.user.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
import com.eventorback.user.domain.dto.request.CertifyEmailRequest;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.RecoverOauthRequest;
import com.eventorback.user.domain.dto.request.SendCodeRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
import com.eventorback.user.domain.dto.request.UpdateUserAttributeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserAuth;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserOauth;
import com.eventorback.user.domain.dto.response.GetUserResponse;
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
	public ResponseEntity<ApiResponse<Page<GetUserListResponse>>> getUsers(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(userService.getUsers(pageable));
	}

	@AuthorizeRole("admin")
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<GetUserByIdentifier>>> searchUserByIdentifier(@RequestParam String keyword) {
		return ApiResponse.createSuccess(userService.searchUserByIdentifier(keyword));
	}

	@AuthorizeRole("admin")
	@GetMapping("/search/userId")
	public ResponseEntity<ApiResponse<List<GetUserByUserId>>> searchUserByUserId(@RequestParam Long userId) {
		return ApiResponse.createSuccess(userService.searchUserByUserId(userId));
	}

	/**
	 * 아이디를 통해 사용자의 토큰 정보를 조회합니다.
	 */
	@GetMapping("/auth/info")
	public ResponseEntity<ApiResponse<GetUserAuth>> getAuthInfoByIdentifier(@RequestParam String identifier) {
		return ApiResponse.createSuccess(userService.getAuthByIdentifier(identifier));
	}

	@PostMapping("/oauth2/info")
	public ResponseEntity<ApiResponse<GetUserOauth>> getOAuthInfoByOauth(@RequestBody OauthDto request) {
		return ApiResponse.createSuccess(userService.getOAuthInfoByOauth(request));
	}

	@AuthorizeRole("admin")
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<GetUserResponse>> getUser(@PathVariable Long userId) {
		return ApiResponse.createSuccess(userService.getUserInfo(userId));
	}

	@AuthorizeRole("admin")
	@PutMapping("/{userId}")
	public ResponseEntity<ApiResponse<Void>> updateUserByAdmin(@PathVariable Long userId,
		@RequestBody UpdateUserRequest request) {
		userService.updateUserByAdmin(userId, request);
		return ApiResponse.createSuccess("회원 정보가 수정 되었습니다.");
	}

	@AuthorizeRole("admin")
	@PutMapping("/{userId}/attribute")
	public ResponseEntity<ApiResponse<Void>> updateUserAttributeByAdmin(@PathVariable Long userId,
		@RequestBody UpdateUserAttributeRequest request) {
		userService.updateUserAttributeByAdmin(userId, request);
		return ApiResponse.createSuccess("회원 특성이 수정 되었습니다.");
	}

	@AuthorizeRole("member")
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<GetUserResponse>> getUserInfo(@CurrentUserId Long userId) {
		return ApiResponse.createSuccess(userService.getUserInfo(userId));
	}

	@PutMapping("/me")
	public ResponseEntity<ApiResponse<Void>> updateUser(@CurrentUserId Long userId,
		@RequestBody UpdateUserRequest request) {
		userService.updateUser(userId, request);
		return ApiResponse.createSuccess("회원 정보가 수정 되었습니다.");
	}

	@DeleteMapping("/me")
	ResponseEntity<ApiResponse<Void>> withdrawUser(@CurrentUserId Long userId) {
		userService.withdrawUser(userId);
		return ApiResponse.createSuccess("회원 탈퇴 되었습니다.");
	}

	@GetMapping("/me/checkRoles")
	public ResponseEntity<ApiResponse<Boolean>> meCheckRoles(@CurrentUser CurrentUserDto currentUser,
		@RequestParam String roleName) {
		return ApiResponse.createSuccess(userService.meCheckRoles(currentUser, roleName));
	}

	@GetMapping("/me/Roles")
	public ResponseEntity<ApiResponse<List<String>>> meRoles(@CurrentUser CurrentUserDto currentUser) {
		return ApiResponse.createSuccess(userService.meRoles(currentUser));
	}

	@PostMapping("/me/checkNickname")
	public ResponseEntity<ApiResponse<Void>> meCheckNickname(@CurrentUserId Long userId,
		@RequestBody CheckNicknameRequest request) {
		return ApiResponse.createSuccess(userService.meCheckNickname(userId, request));
	}

	@PutMapping("/me/loginAt")
	public ResponseEntity<ApiResponse<Void>> updateLoginAt(@RequestBody UpdateLoginAtRequest request) {
		userService.updateLoginAt(request);
		return ApiResponse.createSuccess();
	}

	@PutMapping("/me/password")
	ResponseEntity<ApiResponse<Void>> modifyPassword(@CurrentUserId Long userId,
		@RequestBody ModifyPasswordRequest request) {
		return ApiResponse.createSuccess(userService.modifyPassword(userId, request));
	}

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<Void>> signup(@RequestBody SignUpRequest request) {
		userService.signup(request);
		return ApiResponse.createSuccess("회원 가입 되었습니다.");
	}

	@PostMapping("/signup/oauth2/exists")
	ResponseEntity<ApiResponse<Boolean>> existsByOauth(@RequestBody OauthDto request) {
		return ApiResponse.createSuccess(userService.existsByOauth(request));
	}

	@PostMapping("/signup/checkIdentifier")
	ResponseEntity<ApiResponse<Void>> checkIdentifier(@RequestBody CheckIdentifierRequest request) {
		return ApiResponse.createSuccess(userService.checkIdentifier(request));
	}

	@PostMapping("/signup/checkNickname")
	ResponseEntity<ApiResponse<Void>> checkNickname(@RequestBody CheckNicknameRequest request) {
		return ApiResponse.createSuccess(userService.checkNickname(request));
	}

	@PostMapping("/signup/sendEmail")
	public ResponseEntity<ApiResponse<Boolean>> sendEmail(@RequestBody SendCodeRequest request) {
		boolean isSuccess = mailService.sendMail(request.email(), request.type(), NumberUtil.createRandom(6));
		String message = isSuccess ? request.email() + "로 인증 번호를 전송했습니다." : "이미 인증번호를 전송하였습니다.";
		return ApiResponse.createSuccess(isSuccess, message);
	}

	@PostMapping("/signup/certifyEmail")
	public ResponseEntity<ApiResponse<Boolean>> certifyEmail(@RequestBody CertifyEmailRequest request) {
		boolean codeMatch = mailService.certifyEmail(request);
		String message = codeMatch ? "인증되었습니다." : "인증번호가 일치하지 않습니다.";
		return ApiResponse.createSuccess(codeMatch, message);
	}

	@PostMapping("/me/recover/identifier")
	ResponseEntity<ApiResponse<Void>> recoverIdentifier(@RequestParam String identifier) {
		return ApiResponse.createSuccess(userService.recoverIdentifier(identifier));
	}

	@PostMapping("/me/recover/password")
	ResponseEntity<ApiResponse<Void>> recoverPassword(@RequestParam String identifier) {
		return ApiResponse.createSuccess(userService.recoverPassword(identifier));
	}

	@PostMapping("/me/recover")
	ResponseEntity<ApiResponse<Void>> recover(@RequestParam String identifier) {
		return ApiResponse.createSuccess(userService.recover(identifier));
	}

	@PostMapping("/me/recover/oauth")
	ResponseEntity<ApiResponse<Void>> recoverOauth(@RequestBody RecoverOauthRequest request) {
		return ApiResponse.createSuccess(userService.recoverOauth(request));
	}

	@GetMapping("/oauth2/signup")
	public String nickname(@ModelAttribute("request") SignUpRequest request,
		Model model) {
		model.addAttribute("request", request); // request 정보를 모델에 추가하여 뷰에서 사용할 수 있게 함
		return "oauth/nickname";
	}

}
