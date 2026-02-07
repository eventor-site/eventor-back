package com.eventorback.auth.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eventorback.auth.dto.ReissueTokenDto;
import com.eventorback.auth.service.AuthService;
import com.eventorback.auth.utils.JwtUtils;
import com.eventorback.global.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtUtils jwtUtils;
	private final AuthService authService;
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();

		// 인증이 필요없는 경로들
		return path.startsWith("/back/users/signup/") ||
			path.startsWith("/back/users/recover/") ||
			path.startsWith("/back/oauth2") ||
			path.equals("/back/auth/reissue");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		String accessToken = extractToken(request, "Access-Token");
		String refreshToken = extractToken(request, "Refresh-Token");

		if (accessToken == null || refreshToken == null) {
			filterChain.doFilter(request, response);
			return;
		}

		try {
			// 토큰 검증
			jwtUtils.getClaims(accessToken);
			jwtUtils.getClaims(refreshToken);

			// 인증 정보 설정
			setAuthentication(request, accessToken);

		} catch (ExpiredJwtException ex) {
			// 토큰 재발급 시도
			if (reissueToken(request, response, accessToken, refreshToken)) {
				filterChain.doFilter(request, response);
				return;
			}
		} catch (Exception ex) {
			handleInvalidToken(response);
			return;
		}

		filterChain.doFilter(request, response);
	}

	private String extractToken(HttpServletRequest request, String headerName) {
		String token = request.getHeader(headerName);
		if (token != null) {
			// Bearer 접두사 제거
			if (token.startsWith("Bearer ")) {
				token = token.substring(7);
			} else if (token.startsWith("Bearer+")) {
				token = token.substring(7);
			}
			// 공백 제거
			return token.trim();
		}
		return token;
	}

	private void setAuthentication(HttpServletRequest request, String accessToken) {
		Long userId = jwtUtils.getUserIdFromToken(accessToken);
		List<String> roles = jwtUtils.getRolesFromToken(accessToken);

		// 헤더에 사용자 정보 추가
		request.setAttribute("X-User-userId", userId.toString());
		request.setAttribute("X-User-Roles", roles.toString());
	}

	private boolean reissueToken(HttpServletRequest request, HttpServletResponse response,
		String accessToken, String refreshToken) {
		try {
			ReissueTokenDto reissueRequest = new ReissueTokenDto(accessToken, refreshToken);
			ReissueTokenDto newTokens = authService.reissueToken(reissueRequest);

			// 새 토큰을 응답 헤더에 추가
			response.setHeader("new-access-token", newTokens.accessToken());
			response.setHeader("new-refresh-token", newTokens.refreshToken());

			// 새 토큰으로 인증 설정
			setAuthentication(request, newTokens.accessToken());

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private void handleInvalidToken(HttpServletResponse response) throws IOException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		ApiResponse<Void> apiResponse = new ApiResponse<>(
			HttpStatus.UNAUTHORIZED.name(), null, "인증이 만료되었습니다.");

		String json = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(json);
	}
}