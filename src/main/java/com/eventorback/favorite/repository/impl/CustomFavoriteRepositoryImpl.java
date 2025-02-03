package com.eventorback.favorite.repository.impl;

import static com.eventorback.favorite.domain.entity.QFavorite.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.favorite.domain.dto.response.GetFavoriteResponse;
import com.eventorback.favorite.repository.CustomFavoriteRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFavoriteRepositoryImpl implements CustomFavoriteRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetFavoriteResponse> getFavoritePosts(Pageable pageable, Long userId) {
		List<GetFavoriteResponse> result = queryFactory
			.select(Projections.constructor(
				GetFavoriteResponse.class,
				favorite.favoriteId,
				post.postId,
				post.writer,
				post.title,
				favorite.createdAt))
			.from(favorite)
			.join(favorite.post, post)
			.join(favorite.user, user)
			.join(post.status, status)
			.where(status.name.eq("작성됨").and(user.userId.eq(userId)))
			.orderBy(favorite.favoriteId.desc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(favorite.count())
			.from(favorite)
			.where(favorite.post.status.name.eq("작성됨").and(favorite.post.user.userId.eq(userId)))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
