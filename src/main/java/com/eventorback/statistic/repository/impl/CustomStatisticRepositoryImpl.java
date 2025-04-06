package com.eventorback.statistic.repository.impl;

import static com.eventorback.statistic.domain.entity.QStatistic.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.statistic.domain.dto.response.GetStatistic;
import com.eventorback.statistic.repository.CustomStatisticRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomStatisticRepositoryImpl implements CustomStatisticRepository {
	private final JPAQueryFactory queryFactory;

	public Page<GetStatistic> getStatistics(Pageable pageable) {
		List<GetStatistic> result = queryFactory
			.select(Projections.constructor(
				GetStatistic.class,
				statistic.date,
				statistic.visitorCount,
				statistic.visitedCount,
				statistic.loginCount,
				statistic.signupCount))
			.from(statistic)
			.orderBy(statistic.statisticId.desc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(statistic.count())
			.from(statistic)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
