package com.eventorback.role.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.role.domain.dto.RoleDto;
import com.eventorback.role.domain.entity.Role;
import com.eventorback.role.exception.RoleAlreadyExistsException;
import com.eventorback.role.exception.RoleNotFoundException;
import com.eventorback.role.repository.RoleRepository;
import com.eventorback.role.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;

	@Override
	public Page<RoleDto> getRoles(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return roleRepository.getRoles(PageRequest.of(page, pageSize));
	}

	@Override
	public RoleDto getRole(Long roleId) {
		Role role = roleRepository.findById(roleId).orElseThrow(RoleNotFoundException::new);
		return RoleDto.fromEntity(role);
	}

	@Override
	public void createRole(RoleDto request) {
		if (roleRepository.existsByName(request.name())) {
			throw new RoleAlreadyExistsException();
		}
		roleRepository.save(Role.toEntity(request));
	}

	@Override
	public void updateRole(Long roleId, RoleDto request) {
		Role role = roleRepository.findById(roleId)
			.orElseThrow(RoleNotFoundException::new);

		if (roleRepository.existsByName(request.name())) {
			throw new RoleAlreadyExistsException();
		}

		role.updateRoleName(request.name());
	}

	@Override
	public void deleteRole(Long roleId) {
		roleRepository.deleteById(roleId);
	}
}
