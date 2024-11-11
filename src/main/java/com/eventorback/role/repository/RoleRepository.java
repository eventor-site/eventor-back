package com.eventorback.role.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.role.domain.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
	boolean existsByName(String name);
}