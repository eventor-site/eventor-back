package com.eventorback.userrole.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.userrole.domain.dto.UserRoleDto;

public interface UserRoleService {
	List<UserRoleDto> getUserRoles();

	Page<UserRoleDto> getUserRoles(Pageable pageable);

	UserRoleDto getUserRole(Long getUserRoleId);

	void createUserRole(UserRoleDto request);

	void updateUserRole(Long userRoleId, UserRoleDto request);

	void deleteUserRole(Long userRoleId);

	List<String> getUserRoleNameList();
}
