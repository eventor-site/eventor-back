package com.eventorback.userrole.repository.impl;

import static com.eventorback.role.domain.entity.QRole.*;
import static com.eventorback.userrole.domain.entity.QUserRole.*;

import java.util.List;

import com.eventorback.role.domain.dto.RoleDto;
import com.eventorback.userrole.repository.CustomUserRoleRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRoleRepositoryImpl implements CustomUserRoleRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<RoleDto> getUserRoles(Long userId) {
		return queryFactory
			.select(Projections.constructor(
				RoleDto.class,
				userRole.role.roleId,
				userRole.role.name
			))
			.from(userRole)
			.where(userRole.user.userId.eq(userId))
			.orderBy(userRole.role.name.asc())
			.fetch();
	}

	@Override
	public List<RoleDto> getUnassignedUserRoles(Long userId) {
		List<Long> hasRoleIds = queryFactory
			.select(userRole.role.roleId)
			.from(userRole)
			.where(userRole.user.userId.eq(userId))
			.fetch();

		return queryFactory
			.select(Projections.constructor(
				RoleDto.class,
				role.roleId,
				role.name
			))
			.from(role) // 모든 역할 테이블에서 조회
			.where(role.roleId.notIn(hasRoleIds))
			.fetch();

	}
}
