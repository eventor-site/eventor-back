package com.eventorback.pointhistory.repository.impl;

import static com.eventorback.point.domain.entity.QPoint.*;
import static com.eventorback.pointhistory.domain.entity.QPointHistory.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.Collections;
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
		// 1. 페이징할 유저 ID 리스트 먼저 조회
		List<Long> pagedUserIds = queryFactory
			.select(pointHistory.user.userId)
			.from(pointHistory)
			.where(pointHistory.createdAt.between(startTime, endTime))
			.groupBy(pointHistory.user.userId)
			.orderBy(point.amount.sum().desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 2. 유저 ID 리스트가 비어있을 수 있으므로 체크
		if (pagedUserIds.isEmpty()) {
			return new PageImpl<>(Collections.emptyList(), pageable, 0);
		}

		// 3. 해당 유저들의 상세 정보 및 합계 포인트 조회
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
			.where(pointHistory.user.userId.in(pagedUserIds),
				pointHistory.createdAt.between(startTime, endTime))
			.groupBy(pointHistory.user.userId, pointHistory.user.nickname, pointHistory.user.email)
			.orderBy(point.amount.sum().desc())
			.fetch();

		// 4. 전체 count는 그대로 유지
		Long total = queryFactory
			.select(pointHistory.user.userId.countDistinct())
			.from(pointHistory)
			.where(pointHistory.createdAt.between(startTime, endTime))
			.fetchOne();

		return new PageImpl<>(result, pageable, total != null ? total : 0L);

	}

}
