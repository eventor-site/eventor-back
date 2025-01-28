package com.eventorback.user.domain.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetUserResponse(
	String identifier,
	String name,
	String nickname,
	String email,
	String phone,
	LocalDate birth,
	String gender,
	String statusName,
	String gradeName,
	String userRoles,
	String oauthType,
	LocalDateTime connectTime,
	LocalDateTime createdAt,
	LocalDateTime updatedTime,
	LocalDateTime lastLoginTime
) {
}
