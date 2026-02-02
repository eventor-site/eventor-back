package com.eventorback.auth.dto.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@RedisHash(value = "refresh_token")
public class RefreshToken {
	@Id
	private String refreshTokenValue;

	@Indexed
	private Long userId;
	private List<String> roles;

	@TimeToLive
	private Long expirationTime;

	@Builder
	public RefreshToken(String refreshTokenValue, Long userId, List<String> roles, Long effectiveTime) {
		this.refreshTokenValue = refreshTokenValue;
		this.userId = userId;
		this.roles = roles;
		this.expirationTime = effectiveTime / 1000;
	}
}