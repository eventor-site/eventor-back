package com.eventorback.post.repository.impl;

import static com.eventorback.category.domain.entity.QCategory.*;
import static com.eventorback.grade.domain.entity.QGrade.*;
import static com.eventorback.image.domain.entity.QImage.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.repository.CustomPostRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {
	private final JPAQueryFactory queryFactory;
	private final CategoryRepository categoryRepository;

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
				post.createdAt,
				grade.name))
			.from(post)
			.join(post.status, status)
			.join(post.user, user)
			.join(user.grade, grade)
			.where(status.name.eq("작성됨"))
			.orderBy(post.createdAt.desc())
			.fetch();
	}

	@Override
	public List<GetPostSimpleResponse> getPostsByUserId(Long userId) {
		return queryFactory
			.select(Projections.constructor(
				GetPostSimpleResponse.class,
				post.postId,
				post.writer,
				post.title,
				post.recommendationCount,
				post.viewCount,
				post.createdAt,
				grade.name))
			.from(post)
			.join(post.status, status)
			.join(post.user, user)
			.join(user.grade, grade)
			.where(status.name.eq("작성됨").and(user.userId.eq(userId)))
			.orderBy(post.createdAt.desc())
			.fetch();
	}

	@Override
	public List<GetMainPostResponse> getHotEventPosts() {
		return queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				image.url
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(image)
			.on(image.post.postId.eq(post.postId))
			.on(image.imageId.eq(
				JPAExpressions
					.select(image.imageId.min())  // 가장 작은 imageId 선택
					.from(image)
					.where(image.post.postId.eq(post.postId))
			))
			.where(status.name.eq("작성됨"))
			.orderBy(post.viewCount.desc())  // 조회수 기준 정렬
			.limit(10) // 상위 10개 게시물만 반환
			.fetch();
	}

	@Override
	public List<GetMainPostResponse> getLatestEventPosts() {
		return queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				image.url
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(image)
			.on(image.post.postId.eq(post.postId))
			.on(image.imageId.eq(
				JPAExpressions
					.select(image.imageId.min())  // 가장 작은 imageId 선택
					.from(image)
					.where(image.post.postId.eq(post.postId))
			))
			.where(status.name.eq("작성됨"))
			.orderBy(post.createdAt.desc()) // 조회수 기준 정렬
			.limit(10) // 상위 10개 게시물 제한
			.fetch();
	}

	@Override
	public List<GetMainPostResponse> getRecommendationEventPosts() {
		return queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				image.url // 첫 번째 이미지 URL
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(image)
			.on(image.post.postId.eq(post.postId))
			.on(image.imageId.eq(
				JPAExpressions
					.select(image.imageId.min())  // 가장 작은 imageId 선택
					.from(image)
					.where(image.post.postId.eq(post.postId))
			))
			.on(image.post.postId.eq(post.postId)) // 게시물과 연결된 이미지
			.where(status.name.eq("작성됨"))
			.orderBy(post.recommendationCount.desc())
			.limit(10)  // 상위 10권으로 결과 제한
			.fetch();
	}

	@Override
	public List<GetMainPostResponse> getHotPostsByCategoryName(List<Long> categoryIds) {
		return queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				image.url
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(image)
			.on(image.post.postId.eq(post.postId))
			.on(image.imageId.eq(
				JPAExpressions
					.select(image.imageId.min())  // 가장 작은 imageId 선택
					.from(image)
					.where(image.post.postId.eq(post.postId))
			))
			.where(status.name.eq("작성됨"), post.category.categoryId.in(categoryIds))
			.orderBy(post.viewCount.desc())  // 조회수 기준 정렬
			.limit(10) // 상위 10개 게시물만 반환
			.fetch();
	}

	@Override
	public List<GetPostsByCategoryNameResponse> getPostsByCategoryName(List<Long> categoryIds) {
		return queryFactory
			.select(Projections.constructor(
				GetPostsByCategoryNameResponse.class,
				post.postId,
				post.writer,
				post.title,
				post.recommendationCount,
				post.viewCount,
				post.createdAt,
				grade.name,
				image.url
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.join(post.user, user)
			.join(user.grade, grade)
			.leftJoin(image)
			.on(image.post.postId.eq(post.postId))
			.on(image.imageId.eq(
				JPAExpressions
					.select(image.imageId.min())  // 가장 작은 imageId 선택
					.from(image)
					.where(image.post.postId.eq(post.postId))
			))
			.where(status.name.eq("작성됨"), post.category.categoryId.in(categoryIds))
			.orderBy(post.createdAt.desc())
			.fetch();
	}

	@Override
	public Optional<Post> getPost(Long postId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(post)
				.join(post.category, category).fetchJoin()
				.join(post.status, status).fetchJoin()
				.where(post.postId.eq(postId).and(status.name.eq("작성됨")))
				.fetchOne()
		);
	}

}
