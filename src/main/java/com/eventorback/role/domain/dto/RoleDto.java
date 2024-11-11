package com.eventorback.role.domain.dto;

import com.eventorback.role.domain.entity.Role;

import lombok.Builder;

@Builder
public record RoleDto(
	Long roleId,
	String name) {

	public static RoleDto fromEntity(Role role) {
		return RoleDto.builder()
			.roleId(role.getRoleId())
			.name(role.getName())
			.build();
	}
}
