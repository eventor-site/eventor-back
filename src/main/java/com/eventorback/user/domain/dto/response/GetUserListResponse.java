package com.eventorback.user.domain.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record GetUserListResponse(
	Long userId,
	String nickname,
	String statusName,
	String gradeName,
	List<String> roles
) {
}
