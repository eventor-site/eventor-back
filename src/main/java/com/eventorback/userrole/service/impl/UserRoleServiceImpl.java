package com.eventorback.userrole.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.role.domain.dto.RoleDto;
import com.eventorback.role.domain.entity.Role;
import com.eventorback.role.exception.RoleNotFoundException;
import com.eventorback.role.repository.RoleRepository;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.userrole.domain.entity.UserRole;
import com.eventorback.userrole.repository.UserRoleRepository;
import com.eventorback.userrole.service.UserRoleService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
	private final UserRoleRepository userRoleRepository;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	@Override
	public List<RoleDto> getUserRoles(Long userId) {
		return userRoleRepository.getUserRoles(userId);
	}

	@Override
	public List<RoleDto> getUnassignedUserRoles(Long userId) {
		return userRoleRepository.getUnassignedUserRoles(userId);
	}

	@Override
	public void createUserRole(Long userId, Long roleId) {
		User user = userRepository.getUser(userId).orElseThrow(UserNotFoundException::new);
		Role role = roleRepository.findById(roleId).orElseThrow(RoleNotFoundException::new);
		userRoleRepository.save(UserRole.toEntity(user, role));
	}

	@Override
	public void deleteUserRole(Long userId, Long roleId) {
		userRoleRepository.deleteByUserUserIdAndRoleRoleId(userId, roleId);
	}

}
