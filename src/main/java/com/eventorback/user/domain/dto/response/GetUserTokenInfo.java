package com.eventorback.user.domain.dto.response;

import java.util.List;

import com.eventorback.user.domain.entity.User;

import lombok.Builder;

@Builder
public record GetUserTokenInfo(
	Long userId,
	String identifier,
	String password,
	List<String> roles,
	String statusName
) {
	public static GetUserTokenInfo fromEntity(User user, List<String> roleNames) {
		return GetUserTokenInfo.builder()
			.userId(user.getUserId())
			.identifier(user.getIdentifier())
			.password(user.getPassword())
			.roles(roleNames)
			.statusName(user.getStatus().getName())
			.build();
	}
}

