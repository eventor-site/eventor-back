package com.eventorback.point.repository.impl;

import static com.eventorback.point.domain.entity.QPoint.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.point.domain.dto.response.GetPointResponse;
import com.eventorback.point.domain.entity.Point;
import com.eventorback.point.repository.CustomPointRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPointRepositoryImpl implements CustomPointRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetPointResponse> getPoints(Pageable pageable) {
		List<GetPointResponse> result = queryFactory
			.select(Projections.constructor(
				GetPointResponse.class,
				point.pointId,
				point.name,
				point.amount
			))
			.from(point)
			.orderBy(point.name.asc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(point.count())
			.from(point)
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Optional<GetPointResponse> getPoint(Long pointId) {
		return Optional.ofNullable(queryFactory
			.select(Projections.constructor(
				GetPointResponse.class,
				point.pointId,
				point.name,
				point.amount
			))
			.from(point)
			.where(point.pointId.eq(pointId))
			.fetchOne());
	}

	@Override
	public Point findOrCreatePoint(String name) {
		// 기존 Point 조회
		Point existingPoint = queryFactory
			.selectFrom(point)
			.where(point.name.eq(name))
			.fetchOne();

		if (existingPoint != null) {
			// 이미 존재하는 경우 해당 Point 반환
			return existingPoint;
		} else {
			// 존재하지 않는다면 새로 생성
			Point newPoint = Point.builder()
				.name(name)
				.amount(0L)
				.build();

			// 새 엔티티를 영속화
			entityManager.persist(newPoint);
			return newPoint;
		}

	}
}
