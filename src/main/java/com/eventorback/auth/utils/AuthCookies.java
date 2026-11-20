package com.eventorback.auth.utils;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import com.eventorback.global.config.EventorProperties;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 인증 쿠키. 모두 HttpOnly(스크립트에서 읽을 수 없음), SameSite=Lax, 경로 /api.
 * <ul>
 *     <li>access_token: 액세스 JWT</li>
 *     <li>refresh_token: 리프레시 토큰 원문</li>
 *     <li>auth_ticket: 로그인 중간 단계 티켓(휴면 해제·구글 가입 대기), 경로 /api/auth</li>
 *     <li>oauth_state: 구글 로그인 state 값과 돌아갈 주소, 경로 /api/auth/google</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class AuthCookies {
	public static final String ACCESS = "access_token";
	public static final String REFRESH = "refresh_token";
	public static final String TICKET = "auth_ticket";
	public static final String OAUTH_STATE = "oauth_state";

	private static final String API_PATH = "/api";
	private static final String AUTH_PATH = "/api/auth";
	private static final String GOOGLE_PATH = "/api/auth/google";

	private final EventorProperties properties;

	public void setTokens(HttpServletResponse response, String accessToken, String refreshToken) {
		add(response, ACCESS, accessToken, API_PATH, properties.jwt().refreshTtl());
		if (refreshToken != null) {
			add(response, REFRESH, refreshToken, API_PATH, properties.jwt().refreshTtl());
		}
	}

	public void clearTokens(HttpServletResponse response) {
		add(response, ACCESS, "", API_PATH, Duration.ZERO);
		add(response, REFRESH, "", API_PATH, Duration.ZERO);
	}

	public void setTicket(HttpServletResponse response, String ticket, Duration ttl) {
		add(response, TICKET, ticket, AUTH_PATH, ttl);
	}

	public void clearTicket(HttpServletResponse response) {
		add(response, TICKET, "", AUTH_PATH, Duration.ZERO);
	}

	public void setOauthState(HttpServletResponse response, String value, Duration ttl) {
		add(response, OAUTH_STATE, value, GOOGLE_PATH, ttl);
	}

	public void clearOauthState(HttpServletResponse response) {
		add(response, OAUTH_STATE, "", GOOGLE_PATH, Duration.ZERO);
	}

	public static Optional<String> read(HttpServletRequest request, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return Optional.empty();
		}
		return Arrays.stream(cookies)
			.filter(c -> name.equals(c.getName()))
			.map(Cookie::getValue)
			.filter(v -> v != null && !v.isBlank())
			.findFirst();
	}

	private void add(HttpServletResponse response, String name, String value, String path, Duration maxAge) {
		ResponseCookie cookie = ResponseCookie.from(name, value)
			.httpOnly(true)
			.secure(properties.cookie().secure())
			.sameSite("Lax")
			.path(path)
			.maxAge(maxAge)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
}
