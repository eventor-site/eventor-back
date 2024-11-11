package com.eventorback.statustype.repository.impl;

import static com.eventorback.statustype.domain.entity.QStatusType.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.eventorback.statustype.domain.dto.StatusTypeDto;
import com.eventorback.statustype.repository.CustomStatusTypeRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomStatusTypeRepositoryImpl implements CustomStatusTypeRepository {
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
}
