package com.eventorback.auth.utils;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.eventorback.auth.exception.TokenValidationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰의 생성 및 검증을 담당합니다.
 */
@Slf4j
@Component
public class JwtUtils {
	private final SecretKey secretKey;

	/**
	 * JWT 유틸리티를 초기화합니다.
	 */
	public JwtUtils(@Value("${spring.jwt.secret}") String secret) {
		secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
			Jwts.SIG.HS256.key().build().getAlgorithm());
	}

	/**
	 * JWT 토큰에서 클레임을 추출합니다.
	 */
	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token.replace("Bearer+", ""))
			.getPayload();
	}

	/**
	 * JWT 토큰에서 사용자 ID를 추출합니다.
	 */
	public Long getUserIdFromToken(String token) {
		return getClaims(token).get("userId", Long.class);
	}

	/**
	 * JWT 토큰에서 역할 정보를 추출합니다.
	 */
	public List<String> getRolesFromToken(String token) {
		Claims claims = getClaims(token);
		return ((List<?>)claims.get("roles")).stream()
			.map(Object::toString)
			.toList();
	}

	/**
	 * JWT 토큰에서 토큰 타입을 추출합니다.
	 */
	public String getTokenTypeFromToken(String token) {
		return getClaims(token).get("token-type", String.class);
	}

	/**
	 * JWT 토큰의 유효성을 검증합니다.
	 */
	public void validateToken(String token) {
		try {
			Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token.replace("Bearer+", ""));
		} catch (Exception e) {
			throw new TokenValidationException();
		}
	}

	/**
	 * 새 JWT 토큰을 생성합니다.
	 */
	public String generateAccessToken(Long userId, List<String> roles, Long effectiveTime) {
		String tokenTypePrefix = "Bearer ";
		return tokenTypePrefix + Jwts.builder()
			.claim("token-type", "access")
			.claim("userId", userId)
			.claim("roles", roles)
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + effectiveTime))
			.signWith(secretKey)
			.compact();
	}

	public String generateRefreshToken(Long effectiveTime) {
		String tokenTypePrefix = "Bearer ";
		return tokenTypePrefix + Jwts.builder()
			.claim("token-type", "refresh")
			.issuedAt(new Date(System.currentTimeMillis()))
			.expiration(new Date(System.currentTimeMillis() + effectiveTime))
			.signWith(secretKey)
			.compact();
	}
}
