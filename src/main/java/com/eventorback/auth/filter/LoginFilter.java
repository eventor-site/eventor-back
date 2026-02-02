package com.eventorback.auth.filter;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eventorback.auth.dto.custom.AppCustomUserDetails;
import com.eventorback.auth.dto.entity.RefreshToken;
import com.eventorback.auth.dto.request.LoginRequest;
import com.eventorback.auth.dto.response.LoginResponse;
import com.eventorback.auth.repository.RefreshTokenRepository;
import com.eventorback.auth.utils.JwtUtils;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.global.exception.UserWithdrawAuthenticationException;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
import com.eventorback.user.domain.dto.response.GetUserAuth;
import com.eventorback.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 요청을 처리하는 필터입니다. 이 필터는 사용자의 로그인 요청을 인증하고, 성공적으로 인증된 경우 JWT 를 생성하여 클라이언트에게 반환하며, 리프레시 토큰을 Redis 에 저장합니다.
 */
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	private final Long accessTokenExpiresIn;
	private final Long refreshTokenExpiresIn;
	private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserService userService;

	/**
	 * 생성자입니다. 로그인 필터를 설정합니다.
	 */
	public LoginFilter(
		AuthenticationManager authenticationManager, JwtUtils jwtUtils, Long accessTokenExpiresIn,
		Long refreshTokenExpiresIn, RefreshTokenRepository refreshTokenRepository, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.accessTokenExpiresIn = accessTokenExpiresIn;
		this.refreshTokenExpiresIn = refreshTokenExpiresIn;
		this.refreshTokenRepository = refreshTokenRepository;
		this.userService = userService;
		setFilterProcessesUrl("/back/auth/login");
	}

	/**
	 * 로그인 요청을 인증합니다.
	 */
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		LoginRequest loginRequest;
		try {
			loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
			String identifier = loginRequest.identifier();
			String password = loginRequest.password();

			UsernamePasswordAuthenticationToken authToken
				= new UsernamePasswordAuthenticationToken(identifier, password, null);

			return authenticationManager.authenticate(authToken);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 인증에 성공한 후 호출됩니다. JWT 를 생성하고 응답에 작성합니다.
	 */
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException {
		AppCustomUserDetails appCustomUserDetails = (AppCustomUserDetails)authentication.getPrincipal();
		GetUserAuth user = appCustomUserDetails.getUser();

		String accessToken = jwtUtils.generateAccessToken(user.userId(), user.roles(), accessTokenExpiresIn);
		String refreshToken = jwtUtils.generateRefreshToken(refreshTokenExpiresIn);

		refreshTokenRepository.deleteByUserId(user.userId());

		refreshTokenRepository.save(
			new RefreshToken(refreshToken.replace("Bearer ", ""), user.userId(), user.roles(), refreshTokenExpiresIn));

		userService.updateLoginAt(new UpdateLoginAtRequest(user.userId(), LocalDateTime.now()));

		LoginResponse loginResponse = LoginResponse.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.userStatusName(user.statusName())
			.build();

		String json = objectMapper.writeValueAsString(
			new ApiResponse<>(HttpStatus.OK.name(), loginResponse, null));

		response.setStatus(HttpStatus.OK.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	/**
	 * 인증에 실패한 후 호출됩니다. 오류 메시지를 응답에 작성합니다.
	 */
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		String errorMessage;

		if (failed.getCause() instanceof UserWithdrawAuthenticationException) {
			errorMessage = "{\"message\": \"" + failed.getMessage() + "\"}";
		} else {
			errorMessage = "{\"message\": \"인증에 실패 했습니다.\"}";
		}

		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(errorMessage);
	}
}
