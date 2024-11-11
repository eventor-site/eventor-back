package com.eventorback.status.repository.impl;

import static com.sikyeojoback.status.domain.entity.QStatus.*;
import static com.sikyeojoback.statustype.domain.entity.QStatusType.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sikyeojoback.status.domain.dto.response.GetStatusResponse;
import com.sikyeojoback.status.repository.CustomStatusRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomStatusRepositoryImpl implements CustomStatusRepository {
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
}
