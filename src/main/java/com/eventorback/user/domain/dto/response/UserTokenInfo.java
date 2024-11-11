package com.eventorback.user.domain.dto.response;

import java.util.List;

import com.eventorback.user.domain.entity.User;

import lombok.Builder;

@Builder
public record UserTokenInfo(
	Long userId,
	String password,
	List<String> roles,
	String statusName
) {
	public static UserTokenInfo fromEntity(User user, List<String> roleNames) {
		return UserTokenInfo.builder()
			.userId(user.getUserId())
			.password(user.getPassword())
			.roles(roleNames)
			.statusName(user.getStatus().getName())
			.build();
	}
}

