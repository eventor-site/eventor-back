package com.eventorback.user.repository.impl;

import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;

import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.repository.CustomUserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetUserByAddShopResponse> searchUserById(String keyword) {
		return queryFactory
			.select(user.identifier)
			.from(user)
			.where(user.identifier.contains(keyword))
			.fetch()
			.stream()
			.map(GetUserByAddShopResponse::new)
			.toList();
	}
}
