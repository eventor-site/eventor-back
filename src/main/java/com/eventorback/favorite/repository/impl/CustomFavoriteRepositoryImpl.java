package com.eventorback.favorite.repository.impl;

import static com.eventorback.favorite.domain.entity.QFavorite.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;

import com.eventorback.favorite.domain.dto.response.GetFavoriteResponse;
import com.eventorback.favorite.repository.CustomFavoriteRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFavoriteRepositoryImpl implements CustomFavoriteRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetFavoriteResponse> getFavoritePosts(Long userId) {
		return queryFactory
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
			.fetch();
	}
}
