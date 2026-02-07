package com.eventorback.auth.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.eventorback.auth.dto.ReissueTokenDto;
import com.eventorback.auth.dto.entity.RefreshToken;
import com.eventorback.auth.exception.RefreshTokenNotFoundException;
import com.eventorback.auth.repository.RefreshTokenRepository;
import com.eventorback.auth.utils.JwtUtils;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 인증 및 토큰 관리를 담당하는 서비스 클래스입니다.
 * JWT 를 사용하여 액세스 토큰과 리프레시 토큰을 생성, 갱신하고, Redis 를 통해 리프레시 토큰을 관리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtils jwtUtils;

	@Setter
	@Value("${spring.jwt.access-token.expires-in}")
	private Long accessTokenExpiresIn;

	@Setter
	@Value("${spring.jwt.refresh-token.expires-in}")
	private Long refreshTokenExpiresIn;

	/**
	 * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
	 * 리프레시 토큰이 유효하지 않거나 Redis 에서 해당 토큰이 존재하지 않으면 null 을 반환합니다.
	 */
	public ReissueTokenDto reissueToken(ReissueTokenDto request) {

		// 토큰 검증
		jwtUtils.validateToken(request.refreshToken());

		RefreshToken refreshToken = refreshTokenRepository.findById(request.refreshToken()).orElseThrow(
			RefreshTokenNotFoundException::new);
		Long userId = refreshToken.getUserId();
		List<String> roles = refreshToken.getRoles();
		Long remainingTtl = refreshToken.getExpirationTime() * 1000;

		String newAccessToken = jwtUtils.generateAccessToken(userId, roles, accessTokenExpiresIn);
		String newRefreshToken = jwtUtils.generateRefreshToken(remainingTtl);

		// 기존 재발급 토큰 삭제
		refreshTokenRepository.deleteById(request.refreshToken());

		// 새로 발급한 재발급 토큰 저장
		refreshTokenRepository.save(
			new RefreshToken(newRefreshToken.replace("Bearer ", ""), userId, roles, remainingTtl));

		return ReissueTokenDto.builder()
			.accessToken(newAccessToken.replace("Bearer ", "Bearer+"))
			.refreshToken(newRefreshToken.replace("Bearer ", "Bearer+"))
			.build();
	}

}