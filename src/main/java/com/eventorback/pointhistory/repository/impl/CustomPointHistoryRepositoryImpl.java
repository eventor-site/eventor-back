package com.eventorback.pointhistory.repository.impl;

import static com.eventorback.point.domain.entity.QPoint.*;
import static com.eventorback.pointhistory.domain.entity.QPointHistory.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.pointhistory.domain.dto.response.GetUserPointTotalResponse;
import com.eventorback.pointhistory.repository.CustomPointHistoryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPointHistoryRepositoryImpl implements CustomPointHistoryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetUserPointTotalResponse> getUserPointTotalsByPeriod(LocalDateTime startTime, LocalDateTime endTime,
		Pageable pageable) {
		List<GetUserPointTotalResponse> result = queryFactory
			.select(Projections.constructor(
				GetUserPointTotalResponse.class,
				pointHistory.user.userId,
				pointHistory.user.nickname,
				pointHistory.user.email,
				point.amount.sum()
			))
			.from(pointHistory)
			.join(pointHistory.point, point)
			.join(pointHistory.user, user)
			.where(pointHistory.createdAt.between(startTime, endTime))
			.groupBy(pointHistory.user.userId, pointHistory.user.nickname, pointHistory.user.email)
			.orderBy(point.amount.sum().desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 전체 유저 수 (group 기준 개수)
		Long total = queryFactory
			.select(pointHistory.user.userId.countDistinct())
			.from(pointHistory)
			.where(pointHistory.createdAt.between(startTime, endTime))
			.fetchOne();

		return new PageImpl<>(result, pageable, total != null ? total : 0L);
	}

}
