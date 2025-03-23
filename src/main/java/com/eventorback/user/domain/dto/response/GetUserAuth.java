package com.eventorback.user.domain.dto.response;

import java.util.List;

import com.eventorback.user.domain.entity.User;

import lombok.Builder;

@Builder
public record GetUserAuth(
	Long userId,
	String identifier,
	String password,
	List<String> roles,
	String statusName
) {
	public static GetUserAuth fromEntity(User user) {
		List<String> roles = user.getUserRoles().stream().map(userRole -> userRole.getRole().getName()).toList();

		return GetUserAuth.builder()
			.userId(user.getUserId())
			.identifier(user.getIdentifier())
			.password(user.getPassword())
			.roles(roles)
			.statusName(user.getStatus().getName())
			.build();
	}
}

