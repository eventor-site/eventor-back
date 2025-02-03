package com.eventorback.grade.repository.impl;

import static com.eventorback.grade.domain.entity.QGrade.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.grade.domain.dto.GradeDto;
import com.eventorback.grade.repository.CustomGradeRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomGradeRepositoryImpl implements CustomGradeRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GradeDto> getGrades(Pageable pageable) {
		List<GradeDto> result = queryFactory
			.select(Projections.constructor(
				GradeDto.class,
				grade.gradeId,
				grade.name,
				grade.minAmount,
				grade.maxAmount))
			.from(grade)
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.orderBy(grade.minAmount.asc())
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(grade.count())
			.from(grade)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
