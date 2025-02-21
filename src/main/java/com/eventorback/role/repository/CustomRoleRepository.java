package com.eventorback.role.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.role.domain.dto.RoleDto;

public interface CustomRoleRepository {

	Page<RoleDto> getRoles(Pageable pageable);
}
