package com.eventorback.status.repository.impl;

import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.statustype.domain.entity.QStatusType.*;

import java.util.List;

import com.eventorback.status.domain.dto.response.GetStatusResponse;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.CustomStatusRepository;
import com.eventorback.statustype.domain.entity.StatusType;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomStatusRepositoryImpl implements CustomStatusRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetStatusResponse> getStatusesByStatusTypeName(String statusTypeName) {
		return queryFactory
			.select(status.statusId, status.name)
			.from(status)
			.join(status.statusType, statusType)
			.where(statusType.name.eq(statusTypeName))
			.fetch()
			.stream()
			.map(st -> new GetStatusResponse(st.get(status.statusId), st.get(status.name), st.get(statusType.name)))
			.toList();
	}

	@Override
	public Status findOrCreateStatus(String statusTypeName, String statusName) {
		// 기존 Status 조회
		Status existingStatus = queryFactory
			.selectFrom(status)
			.join(status.statusType, statusType)
			.where(
				statusType.name.eq(statusTypeName),
				status.name.eq(statusName)
			)
			.fetchOne();

		if (existingStatus != null) {
			// 이미 존재하는 경우 해당 Status 반환
			return existingStatus;
		}

		// 해당 StatusType 조회
		StatusType existingStatusType = queryFactory
			.selectFrom(statusType)
			.where(statusType.name.eq(statusTypeName))
			.fetchOne();

		if (existingStatusType == null) {
			// 존재하지 않는다면 새로 생성
			StatusType newStatusType = StatusType.builder()
				.name(statusTypeName)
				.build();

			// 새 엔티티를 영속화
			entityManager.persist(newStatusType);

			existingStatusType = queryFactory
				.selectFrom(statusType)
				.where(statusType.name.eq(statusTypeName))
				.fetchOne();
		}

		// 새 Status 생성 및 저장
		Status newStatus = Status.builder()
			.name(statusName)
			.statusType(existingStatusType)
			.build();

		entityManager.persist(newStatus); // 새 엔티티 저장
		return newStatus;
	}
}
