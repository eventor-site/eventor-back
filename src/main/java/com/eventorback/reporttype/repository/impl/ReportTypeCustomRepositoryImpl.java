package com.eventorback.reporttype.repository.impl;

import static com.eventorback.reporttype.domain.entity.QReportType.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.reporttype.domain.dto.ReportTypeDto;
import com.eventorback.reporttype.repository.ReportTypeCustomRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportTypeCustomRepositoryImpl implements ReportTypeCustomRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<ReportTypeDto> getReportTypes() {
		return queryFactory
			.select(Projections.constructor(
				ReportTypeDto.class,
				reportType.reportTypeId,
				reportType.name))
			.from(reportType)
			.orderBy(reportType.reportTypeId.asc())
			.fetch();
	}

	@Override
	public Page<ReportTypeDto> getReportTypes(Pageable pageable) {
		List<ReportTypeDto> result = queryFactory
			.select(Projections.constructor(
				ReportTypeDto.class,
				reportType.reportTypeId,
				reportType.name))
			.from(reportType)
			.orderBy(reportType.reportTypeId.asc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(reportType.count())
			.from(reportType)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
