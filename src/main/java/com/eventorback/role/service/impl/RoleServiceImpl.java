package com.eventorback.role.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sikyeojoback.role.domain.dto.RoleDto;
import com.sikyeojoback.role.domain.entity.Role;
import com.sikyeojoback.role.exception.RoleAlreadyExistsException;
import com.sikyeojoback.role.exception.RoleNotFoundException;
import com.sikyeojoback.role.repository.RoleRepository;
import com.sikyeojoback.role.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;

	@Override
	public List<RoleDto> getRoles() {
		return roleRepository.findAll().stream().map(RoleDto::fromEntity).toList();
	}

	@Override
	public Page<RoleDto> getRoles(Pageable pageable) {
		return null;
	}

	@Override
	public RoleDto getRole(Long roleId) {
		Role role = roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));
		return RoleDto.fromEntity(role);
	}

	@Override
	public void createRole(RoleDto request) {
		if (roleRepository.existsByName(request.name())) {
			throw new RoleAlreadyExistsException(request.name());
		}
		roleRepository.save(Role.toEntity(request));
	}

	@Override
	public void updateRole(Long roleId, RoleDto request) {
		Role role = roleRepository.findById(roleId)
			.orElseThrow(() -> new RoleNotFoundException(roleId));

		if (roleRepository.existsByName(request.name())) {
			throw new RoleAlreadyExistsException(request.name());
		}

		role.updateRoleName(request.name());
	}

	@Override
	public void deleteRole(Long roleId) {
		roleRepository.deleteById(roleId);
	}
}
