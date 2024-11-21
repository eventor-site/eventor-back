package com.eventorback.post.repository.impl;

import static com.eventorback.category.domain.entity.QCategory.*;
import static com.eventorback.image.domain.entity.QImage.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.repository.CustomPostRepository;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetPostSimpleResponse> getPostsByCategoryName(String categoryName) {
		return queryFactory
			.select(Projections.constructor(
				GetPostSimpleResponse.class,
				post.postId,
				post.writer,
				post.title,
				post.recommendationCount,
				post.viewCount,
				post.createdAt))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.where(category.name.eq(categoryName).and(status.name.eq("게시글 작성됨")))
			.orderBy(post.createdAt.desc())
			.fetch();
	}

	@Override
	public List<GetPostSimpleResponse> getPosts() {
		return queryFactory
			.select(Projections.constructor(
				GetPostSimpleResponse.class,
				post.postId,
				post.writer,
				post.title,
				post.recommendationCount,
				post.viewCount,
				post.createdAt))
			.from(post)
			.join(post.user, user)
			.join(post.status, status)
			.where(status.name.eq("게시글 작성됨"))
			.orderBy(post.createdAt.desc())
			.fetch();
	}

	@Override
	public Optional<GetPostResponse> getPost(Long postId) {
		List<String> imageUrls = queryFactory
			.select(image.url)
			.from(image)
			.where(image.post.postId.eq(postId))
			.fetch(); // 해당 게시물의 이미지 URL 리스트 가져오기

		return Optional.ofNullable(
			queryFactory
				.select(Projections.constructor(
					GetPostResponse.class,
					post.postId,
					category.name,
					post.writer,
					post.title,
					post.content,
					post.recommendationCount,
					post.viewCount,
					post.createdAt,
					post.isNotification,
					(Expression<?>)imageUrls))
				.from(post)
				.join(post.category, category)
				.where(post.postId.eq(postId).and(status.name.eq("게시글 작성됨")))
				.fetchOne() // 단일 결과를 반환
		);
	}

}
