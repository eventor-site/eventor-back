package com.eventorback.userstop.repository.impl;

import static com.eventorback.userstop.domain.entity.QUserStop.*;
import static com.querydsl.core.types.ExpressionUtils.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.userstop.domain.dto.response.GetUserStopByUserIdResponse;
import com.eventorback.userstop.domain.dto.response.GetUserStopResponse;
import com.eventorback.userstop.repository.UserStopCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserStopCustomRepositoryImpl implements UserStopCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetUserStopResponse> getUserStops(Pageable pageable) {

		List<GetUserStopResponse> result = queryFactory
			.select(Projections.constructor(
				GetUserStopResponse.class,
				userStop.userStopId,
				userStop.user.userId,
				userStop.reportType.name,
				userStop.stopDay,
				userStop.startTime,
				userStop.endTime
			))
			.from(userStop)
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(userStop.count())
			.from(userStop)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

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
