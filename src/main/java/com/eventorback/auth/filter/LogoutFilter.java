package com.eventorback.auth.filter;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.eventorback.auth.repository.RefreshTokenRepository;
import com.eventorback.global.util.CookieUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 로그아웃 요청을 처리하는 커스텀 필터입니다.
 */
@RequiredArgsConstructor
public class LogoutFilter extends GenericFilterBean {
	private final CookieUtils cookieUtils;
	private final RefreshTokenRepository refreshTokenRepository;

	/**
	 * 요청이 로그아웃 요청인지 확인하고 처리합니다.
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException {
		doFilter((HttpServletRequest)servletRequest, (HttpServletResponse)servletResponse, filterChain);
	}

	/**
	 * HTTP 요청을 필터링하고 로그아웃 처리를 수행합니다.
	 */
	private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {
		String requestUri = request.getRequestURI();
		String requestMethod = request.getMethod();
		String refreshToken = request.getHeader("refresh-token");

		if (!requestUri.matches("/back/auth/logout") || !requestMethod.equals("POST")) {
			filterChain.doFilter(request, response);
			return;
		}

		if (refreshToken == null) {
			response.setStatus(HttpServletResponse.SC_OK);
			return;
		}

		refreshTokenRepository.deleteById(refreshToken.replace("Bearer+", ""));

		// access, refresh 토큰을 만료시킵니다.
		Cookie cookie = cookieUtils.expireCookie("access-token");
		response.addCookie(cookie);

		cookie = cookieUtils.expireCookie("refresh-token");
		response.addCookie(cookie);
		response.setStatus(HttpServletResponse.SC_OK);

	}
}
