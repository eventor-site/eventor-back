package com.eventorback.userrole.service;

import java.util.List;

import com.eventorback.role.domain.dto.RoleDto;

public interface UserRoleService {

	List<RoleDto> getUserRoles(Long userId);

	List<RoleDto> getUnassignedUserRoles(Long userId);

	void createUserRole(Long userId, Long roleId);

	void deleteUserRole(Long userId, Long roleId);

}
