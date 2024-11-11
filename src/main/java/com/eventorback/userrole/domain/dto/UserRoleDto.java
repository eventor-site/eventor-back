package com.eventorback.userrole.domain.dto;

import com.sikyeojoback.userrole.domain.entity.UserRole;

import lombok.Builder;

@Builder
public record UserRoleDto(
	Long userId,
	Long roleId) {

	public static UserRoleDto fromEntity(UserRole userRole) {
		return UserRoleDto.builder()
			.userId(userRole.getUser().getUserId())
			.roleId(userRole.getRole().getRoleId())
			.build();
	}
}
