package com.eventorback.userstop.repository.impl;

import static com.eventorback.userstop.domain.entity.QUserStop.*;
import static com.querydsl.core.types.ExpressionUtils.*;

import java.util.List;

import com.eventorback.userstop.domain.dto.response.GetUserStopByUserIdResponse;
import com.eventorback.userstop.repository.CustomUserStopRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserStopRepositoryImpl implements CustomUserStopRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetUserStopByUserIdResponse> getUserStopByUserId(Long userId) {
		return queryFactory
			.select(Projections.constructor(
				GetUserStopByUserIdResponse.class,
				userStop.reportType.name,
				count(userStop)))
			.from(userStop)
			.where(userStop.user.userId.eq(userId))
			.groupBy(userStop.reportType.name)
			.fetch();
	}
}
