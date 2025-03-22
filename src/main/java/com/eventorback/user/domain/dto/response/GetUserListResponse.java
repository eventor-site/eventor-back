package com.eventorback.user.domain.dto.response;

import java.util.List;

import com.eventorback.user.domain.entity.User;

import lombok.Builder;

@Builder
public record GetUserListResponse(
	Long userId,
	String nickname,
	String statusName,
	String gradeName,
	List<String> roles
) {
	public static GetUserListResponse fromEntity(User user) {
		List<String> roles = new java.util.ArrayList<>(
			user.getUserRoles().stream().map(userRole -> userRole.getRole().getName())
				.toList());
		roles.sort(String::compareTo);

		return GetUserListResponse.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.statusName(user.getStatus().getName())
			.gradeName(user.getGrade().getName())
			.roles(roles)
			.build();
	}
}
