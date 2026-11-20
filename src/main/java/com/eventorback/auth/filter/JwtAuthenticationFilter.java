package com.eventorback.auth.filter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eventorback.auth.service.TokenService;
import com.eventorback.auth.utils.AuthCookies;
import com.eventorback.auth.utils.JwtUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 쿠키의 액세스 토큰으로 로그인 사용자를 알아내 요청 속성에 넣는다.
 * 액세스 토큰이 없거나 만료됐으면 리프레시 토큰으로 조용히 재발급하고 새 쿠키를 내려준다.
 * 인증이 안 돼도 요청은 막지 않는다(비로그인 허용 API가 많다). 권한이 필요한 API는 {@code @AuthorizeRole}이 막는다.
 * <p>
 * 요청 속성 {@link #ATTR_USER_ID}, {@link #ATTR_ROLES}는 {@code @CurrentUser}, {@code @CurrentUserId},
 * {@code @AuthorizeRole}이 읽는다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	public static final String ATTR_USER_ID = "X-User-userId";
	public static final String ATTR_ROLES = "X-User-Roles";

	private final JwtUtils jwtUtils;
	private final TokenService tokenService;
	private final AuthCookies authCookies;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return !request.getRequestURI().startsWith("/api/");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException {

		Optional<String> accessToken = AuthCookies.read(request, AuthCookies.ACCESS);
		if (accessToken.isPresent()) {
			try {
				Claims claims = jwtUtils.parse(accessToken.get(), JwtUtils.TYPE_ACCESS);
				authenticate(request, JwtUtils.userId(claims), JwtUtils.roles(claims));
				chain.doFilter(request, response);
				return;
			} catch (JwtException | IllegalArgumentException e) {
				// 만료·위조 → 아래에서 리프레시 토큰으로 재발급 시도
			}
		}

		Optional<String> refreshToken = AuthCookies.read(request, AuthCookies.REFRESH);
		if (refreshToken.isPresent()) {
			Optional<TokenService.Session> session = tokenService.refresh(refreshToken.get());
			if (session.isPresent()) {
				TokenService.Session s = session.get();
				authCookies.setTokens(response, s.accessToken(), s.refreshToken());
				authenticate(request, s.userId(), s.roles());
			} else {
				authCookies.clearTokens(response);
			}
		} else if (accessToken.isPresent()) {
			authCookies.clearTokens(response);
		}

		chain.doFilter(request, response);
	}

	private void authenticate(HttpServletRequest request, Long userId, List<String> roles) {
		request.setAttribute(ATTR_USER_ID, String.valueOf(userId));
		request.setAttribute(ATTR_ROLES, roles.toString());
	}
}
