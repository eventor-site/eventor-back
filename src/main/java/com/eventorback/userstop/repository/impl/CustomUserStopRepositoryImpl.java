package com.eventorback.userstop.repository.impl;

import static com.eventorback.userstop.domain.entity.QUserStop.*;
import static com.querydsl.core.types.ExpressionUtils.*;

import java.util.List;

import com.eventorback.userstop.domain.dto.response.GetUserStopByIdentifierResponse;
import com.eventorback.userstop.repository.CustomUserStopRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserStopRepositoryImpl implements CustomUserStopRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetUserStopByIdentifierResponse> getUserStopByIdentifier(String identifier) {
		return queryFactory
			.select(Projections.constructor(
				GetUserStopByIdentifierResponse.class,
				userStop.reportType.name,
				count(userStop)))
			.from(userStop)
			.where(userStop.user.identifier.eq(identifier))
			.groupBy(userStop.reportType.name)
			.fetch();
	}
}
