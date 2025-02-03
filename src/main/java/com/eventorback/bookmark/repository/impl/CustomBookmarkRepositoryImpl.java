package com.eventorback.bookmark.repository.impl;

import static com.eventorback.bookmark.domain.entity.QBookmark.*;
import static com.eventorback.category.domain.entity.QCategory.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

	@Override
	public Page<GetBookmarkResponse> getBookmarksByUserId(Pageable pageable, Long userId) {
		List<GetBookmarkResponse> result = queryFactory
			.select(Projections.constructor(
				GetBookmarkResponse.class,
				bookmark.bookmarkId,
				category.name))
			.from(bookmark)
			.join(bookmark.user, user)
			.join(bookmark.category, category)
			.where(user.userId.eq(userId))
			.orderBy(category.name.asc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(bookmark.count())
			.from(bookmark)
			.where(bookmark.user.userId.eq(userId))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
