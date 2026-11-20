package com.eventorback.auth.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.auth.dto.request.LoginRequest;
import com.eventorback.auth.dto.response.LoginResponse;
import com.eventorback.auth.google.GoogleOAuthClient;
import com.eventorback.auth.service.AuthService;
import com.eventorback.auth.utils.AuthCookies;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 API(/api/auth). 토큰은 모두 HttpOnly 쿠키로 주고받는다.
 * <p>
 * 구글 로그인 흐름: 프론트가 {@code /api/auth/google?redirect=/원래화면}으로 이동 → 구글 동의 →
 * {@code /api/auth/google/callback} → 결과에 따라 프론트 화면으로 302.
 * 콜백 주소는 요청의 호스트로 만든다(로컬 http://localhost:9080, 운영 https://eventor.duckdns.org).
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
	private static final Duration STATE_TTL = Duration.ofMinutes(10);
	private static final String CALLBACK_PATH = "/api/auth/google/callback";

	private final AuthService authService;
	private final com.eventorback.auth.service.AccountRecoveryService accountRecoveryService;
	private final GoogleOAuthClient googleOAuthClient;
	private final AuthCookies authCookies;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request,
		HttpServletResponse response) {
		return ApiResponse.createSuccess(authService.login(request, response));
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request, HttpServletResponse response) {
		authService.logout(request, response);
		return ApiResponse.createSuccess();
	}

	/** 현재 로그인 상태. 비로그인이면 data가 null */
	@GetMapping("/session")
	public ResponseEntity<ApiResponse<Map<String, Object>>> session(@CurrentUser CurrentUserDto currentUser) {
		if (currentUser == null || currentUser.userId() == null) {
			return ApiResponse.createSuccess(null);
		}
		List<String> roles = currentUser.roles() == null ? List.of() : currentUser.roles();
		return ApiResponse.createSuccess(Map.of("userId", currentUser.userId(), "roles", roles));
	}

	@PostMapping("/dormant/activate")
	public ResponseEntity<ApiResponse<Void>> activateDormant(HttpServletRequest request,
		HttpServletResponse response) {
		authService.activateDormant(request, response);
		return ApiResponse.createSuccess("휴면 상태가 해제되었습니다.");
	}

	// ===== 아이디 찾기·비밀번호 재설정 =====

	public record FindIdentifierRequest(String email) {
	}

	public record ResetCodeRequest(String identifier) {
	}

	public record ResetPasswordRequest(String identifier, String code, String newPassword) {
	}

	@PostMapping("/identifier/find")
	public ResponseEntity<ApiResponse<Void>> findIdentifier(@RequestBody FindIdentifierRequest body) {
		return ApiResponse.createSuccess(accountRecoveryService.findIdentifiers(body.email()));
	}

	@PostMapping("/password/code")
	public ResponseEntity<ApiResponse<Void>> sendResetCode(@RequestBody ResetCodeRequest body) {
		return ApiResponse.createSuccess(accountRecoveryService.sendResetCode(body.identifier()));
	}

	@PostMapping("/password/reset")
	public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest body) {
		accountRecoveryService.resetPassword(body.identifier(), body.code(), body.newPassword());
		return ApiResponse.createSuccess("비밀번호를 바꿨어요. 새 비밀번호로 로그인해 주세요.");
	}

	// ===== 구글 =====

	@GetMapping("/google")
	public ResponseEntity<Void> googleAuthorize(@RequestParam(defaultValue = "/") String redirect,
		HttpServletResponse response) {
		String state = AuthService.newState();
		String safeRedirect = safeRedirect(redirect);
		authCookies.setOauthState(response, state + "|" + URLEncoder.encode(safeRedirect, StandardCharsets.UTF_8),
			STATE_TTL);
		return redirect(googleOAuthClient.authorizeUrl(callbackUri(), state));
	}

	@GetMapping("/google/callback")
	public ResponseEntity<Void> googleCallback(@RequestParam(required = false) String code,
		@RequestParam(required = false) String state, @RequestParam(required = false) String error,
		HttpServletRequest request, HttpServletResponse response) {

		String saved = AuthCookies.read(request, AuthCookies.OAUTH_STATE).orElse(null);
		authCookies.clearOauthState(response);

		if (error != null || code == null || state == null || saved == null || !saved.contains("|")) {
			return redirect("/auth/login?error=google");
		}
		String savedState = saved.substring(0, saved.indexOf('|'));
		String redirectPath = safeRedirect(URLDecoder.decode(saved.substring(saved.indexOf('|') + 1),
			StandardCharsets.UTF_8));
		if (!savedState.equals(state)) {
			log.warn("구글 로그인 state 불일치");
			return redirect("/auth/login?error=google");
		}

		try {
			GoogleOAuthClient.GoogleProfile profile = googleOAuthClient.fetchProfile(code, callbackUri());
			return switch (authService.googleLogin(profile, response)) {
				case LOGGED_IN -> redirect(redirectPath);
				case DORMANT -> redirect("/auth/dormant");
				case SIGNUP_REQUIRED -> redirect("/auth/google/signup");
				case WITHDRAWN -> redirect("/auth/login?error=withdrawn");
			};
		} catch (GoogleOAuthClient.GoogleLoginException e) {
			return redirect("/auth/login?error=google");
		}
	}

	@GetMapping("/google/pending")
	public ResponseEntity<ApiResponse<Map<String, String>>> googlePending(HttpServletRequest request) {
		return ApiResponse.createSuccess(authService.googlePending(request));
	}

	public record GoogleSignupRequest(String nickname) {
	}

	@PostMapping("/google/signup")
	public ResponseEntity<ApiResponse<Void>> googleSignup(@RequestBody GoogleSignupRequest body,
		HttpServletRequest request, HttpServletResponse response) {
		authService.googleSignup(body.nickname(), request, response);
		return ApiResponse.createSuccess("가입이 완료되었습니다.");
	}

	private static String callbackUri() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(CALLBACK_PATH).toUriString();
	}

	/** 로그인 후 돌아갈 주소는 같은 사이트의 경로만 허용한다(외부 주소로 보내는 오픈 리다이렉트 방지) */
	static String safeRedirect(String redirect) {
		if (redirect == null || !redirect.startsWith("/") || redirect.startsWith("//") || redirect.contains("\\")
			|| redirect.startsWith("/api/")) {
			return "/";
		}
		return redirect;
	}

	private static ResponseEntity<Void> redirect(String location) {
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, location).build();
	}
}
