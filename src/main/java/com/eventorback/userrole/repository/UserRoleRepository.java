package com.eventorback.userrole.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.userrole.domain.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
	List<UserRole> findAllByUserUserId(Long userId);
}