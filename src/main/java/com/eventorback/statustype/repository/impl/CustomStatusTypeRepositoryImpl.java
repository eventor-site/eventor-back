package com.eventorback.statustype.repository.impl;

import static com.eventorback.statustype.domain.entity.QStatusType.*;

import java.util.List;

import com.eventorback.statustype.domain.dto.StatusTypeDto;
import com.eventorback.statustype.domain.entity.StatusType;
import com.eventorback.statustype.repository.CustomStatusTypeRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomStatusTypeRepositoryImpl implements CustomStatusTypeRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private final JPAQueryFactory queryFactory;

	@Override
	public List<StatusTypeDto> searchStatusTypes(String keyword) {
		return queryFactory
			.select(statusType.statusTypeId, statusType.name)
			.from(statusType)
			.where(statusType.name.contains(keyword))
			.fetch()
			.stream()
			.map(st -> new StatusTypeDto(st.get(statusType.statusTypeId), st.get(statusType.name)))
			.toList();
	}

	@Override
	public StatusType findOrCreateStatusType(String statusTypeName) {
		// 먼저 statusTypeName 으로 엔티티를 조회
		StatusType existingStatusType = queryFactory
			.selectFrom(statusType)
			.where(statusType.name.eq(statusTypeName))
			.fetchOne();

		// 존재한다면 해당 엔티티를 반환
		if (existingStatusType != null) {
			return existingStatusType;
		}

		// 존재하지 않는다면 새로 생성
		StatusType newStatusType = StatusType.builder()
			.name(statusTypeName)
			.build();

		// 새 엔티티를 영속화
		entityManager.persist(newStatusType);

		return newStatusType;
	}

}
