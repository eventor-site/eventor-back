package com.eventorback.userrole.repository;

import java.util.List;

import com.eventorback.role.domain.dto.RoleDto;

public interface CustomUserRoleRepository {

	List<RoleDto> getUserRoles(Long userId);

	List<RoleDto> getUnassignedUserRoles(Long userId);
}
