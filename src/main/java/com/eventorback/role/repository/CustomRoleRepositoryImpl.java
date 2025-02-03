package com.eventorback.role.repository;

import static com.eventorback.role.domain.entity.QRole.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.role.domain.dto.RoleDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomRoleRepositoryImpl implements CustomRoleRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<RoleDto> getRoles(Pageable pageable) {
		List<RoleDto> result = queryFactory
			.select(Projections.constructor(
				RoleDto.class,
				role.roleId,
				role.name))
			.from(role)
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.orderBy(role.roleId.asc())
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(role.count())
			.from(role)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
