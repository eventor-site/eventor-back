package com.eventorback.user.domain.dto.response;

import com.eventorback.user.domain.entity.User;

import lombok.Builder;

@Builder
public record GetOauthResponse(
	String username,
	String name,
	String email,
	String role
) {
	public static GetOauthResponse fromEntity(User user) {
		return GetOauthResponse.builder()
			.username(null)
			.name(user.getName())
			.email(user.getEmail())
			.role("member")
			.build();
	}
}
