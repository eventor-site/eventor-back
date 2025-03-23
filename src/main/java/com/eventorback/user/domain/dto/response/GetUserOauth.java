package com.eventorback.user.domain.dto.response;

import java.util.List;

import com.eventorback.user.domain.entity.User;

import lombok.Builder;

@Builder
public record GetUserOauth(
	Long userId,
	String oauthId,
	List<String> roles,
	String statusName
) {
	public static GetUserOauth fromEntity(User user) {
		List<String> roles = user.getUserRoles().stream().map(userRole -> userRole.getRole().getName()).toList();

		return GetUserOauth.builder()
			.userId(user.getUserId())
			.oauthId(user.getOauthId())
			.roles(roles)
			.statusName(user.getStatus().getName())
			.build();
	}
}

