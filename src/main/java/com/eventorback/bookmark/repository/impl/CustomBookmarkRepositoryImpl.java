package com.eventorback.bookmark.repository.impl;

import static com.eventorback.bookmark.domain.entity.QBookmark.*;
import static com.eventorback.category.domain.entity.QCategory.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;

import com.eventorback.bookmark.domain.dto.response.GetBookmarkResponse;
import com.eventorback.bookmark.repository.CustomBookmarkRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomBookmarkRepositoryImpl implements CustomBookmarkRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetBookmarkResponse> getBookmarksByUserId(Long userId) {
		return queryFactory
			.select(Projections.constructor(
				GetBookmarkResponse.class,
				bookmark.bookmarkId,
				category.name))
			.from(bookmark)
			.join(bookmark.user, user)
			.join(bookmark.category, category)
			.where(user.userId.eq(userId))
			.orderBy(category.name.asc())
			.fetch();
	}
}
