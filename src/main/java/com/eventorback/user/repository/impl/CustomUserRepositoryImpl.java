package com.eventorback.user.repository.impl;

import static com.sikyeojoback.user.domain.entity.QUser.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sikyeojoback.user.domain.dto.response.GetUserByAddShopResponse;
import com.sikyeojoback.user.repository.CustomUserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetUserByAddShopResponse> searchUserById(String keyword) {
		return queryFactory
			.select(user.id)
			.from(user)
			.where(user.id.contains(keyword))
			.fetch()
			.stream()
			.map(GetUserByAddShopResponse::new)
			.toList();
	}
}
