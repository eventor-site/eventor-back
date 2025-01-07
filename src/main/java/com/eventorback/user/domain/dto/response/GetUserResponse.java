package com.eventorback.user.domain.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.eventorback.status.domain.entity.Status;
import com.eventorback.user.domain.entity.User;
import com.eventorback.usergrade.domain.entity.UserGrade;

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
	String userGradeName,
	String userRoles,
	LocalDateTime createdAt,
	LocalDateTime updatedTime,
	LocalDateTime lastLoginTime
) {
	public static GetUserResponse fromEntity(User user, UserGrade userGrade, Status status) {
		return GetUserResponse.builder()
			.identifier(user.getIdentifier())
			.name(user.getName())
			.nickname(user.getNickname())
			.email(user.getEmail())
			.phone(user.getPhone())
			.birth(user.getBirth())
			.gender(user.getGender())
			.statusName(status.getName())
			.userGradeName(userGrade.getName())
			.createdAt(user.getCreatedAt())
			.updatedTime(user.getUpdatedTime())
			.lastLoginTime(user.getLastLoginTime())
			.build();
	}
}
