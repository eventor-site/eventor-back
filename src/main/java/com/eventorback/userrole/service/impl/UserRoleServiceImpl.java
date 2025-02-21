package com.eventorback.userrole.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.userrole.domain.dto.UserRoleDto;
import com.eventorback.userrole.domain.entity.UserRole;
import com.eventorback.userrole.exception.UserRoleNotFoundException;
import com.eventorback.userrole.repository.UserRoleRepository;
import com.eventorback.userrole.service.UserRoleService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
	private final UserRoleRepository userRoleRepository;

	@Override
	public List<UserRoleDto> getUserRoles() {
		return userRoleRepository.findAll().stream().map(UserRoleDto::fromEntity).toList();
	}

	@Override
	public Page<UserRoleDto> getUserRoles(Pageable pageable) {
		return null;
	}

	@Override
	public UserRoleDto getUserRole(Long userRoleId) {
		UserRole userRole = userRoleRepository.findById(userRoleId).orElseThrow(UserRoleNotFoundException::new);
		return UserRoleDto.fromEntity(userRole);
	}

	@Override
	public void createUserRole(UserRoleDto request) {

	}

	@Override
	public void updateUserRole(Long userRoleId, UserRoleDto request) {

	}

	@Override
	public void deleteUserRole(Long userRoleId) {
		userRoleRepository.deleteById(userRoleId);
	}

	@Override
	public List<String> getUserRoleNameList() {
		return List.of();
	}
}
