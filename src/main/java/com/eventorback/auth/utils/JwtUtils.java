package com.eventorback.auth.utils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.eventorback.global.config.EventorProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT 생성·검증. 액세스 토큰과, 로그인 중간 단계에서 쓰는 짧은 "티켓"(휴면 해제, 구글 가입 대기)을 만든다.
 * 리프레시 토큰은 JWT가 아니라 임의 문자열이다({@code TokenService}).
 */
@Component
public class JwtUtils {
	public static final String TYPE_ACCESS = "access";
	private static final String CLAIM_TYPE = "token-type";

	private final SecretKey secretKey;

	public JwtUtils(EventorProperties properties) {
		this.secretKey = Keys.hmacShaKeyFor(properties.jwt().secret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(Long userId, List<String> roles, Duration ttl) {
		return Jwts.builder()
			.claim(CLAIM_TYPE, TYPE_ACCESS)
			.claim("userId", userId)
			.claim("roles", roles)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ttl.toMillis()))
			.signWith(secretKey)
			.compact();
	}

	/**
	 * 정해진 용도(type)의 짧은 티켓을 만든다. 다른 용도의 토큰으로는 검증을 통과하지 못한다.
	 */
	public String generateTicket(String type, Map<String, Object> claims, Duration ttl) {
		return Jwts.builder()
			.claims(claims)
			.claim(CLAIM_TYPE, type)
			.issuedAt(new Date())
			.expiration(new Date(System.currentTimeMillis() + ttl.toMillis()))
			.signWith(secretKey)
			.compact();
	}

	/**
	 * 서명·만료·용도를 검증하고 클레임을 돌려준다.
	 *
	 * @throws JwtException 서명이 틀리거나 만료됐거나 용도가 다를 때({@link io.jsonwebtoken.ExpiredJwtException} 포함)
	 */
	public Claims parse(String token, String expectedType) {
		Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
		if (!expectedType.equals(claims.get(CLAIM_TYPE, String.class))) {
			throw new JwtException("토큰 용도가 다릅니다: " + expectedType);
		}
		return claims;
	}

	public static Long userId(Claims claims) {
		return claims.get("userId", Long.class);
	}

	public static List<String> roles(Claims claims) {
		Object roles = claims.get("roles");
		if (roles instanceof List<?> list) {
			return list.stream().map(String::valueOf).toList();
		}
		return List.of();
	}
}
