package com.eventorback.post.repository.impl;

import static com.eventorback.category.domain.entity.QCategory.*;
import static com.eventorback.grade.domain.entity.QGrade.*;
import static com.eventorback.image.domain.entity.QImage.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.postview.domain.entity.QPostView.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.post.domain.dto.response.GetTempPostResponse;
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

	@Override
	public Page<GetPostSimpleResponse> getPosts(Pageable pageable) {
		List<GetPostSimpleResponse> result = queryFactory
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
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(post.count())
			.from(post)
			.where(status.name.eq("작성됨"))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Page<GetPostSimpleResponse> getPostsByUserId(Pageable pageable, Long userId) {
		List<GetPostSimpleResponse> result = queryFactory
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
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(post.count())
			.from(post)
			.where(status.name.eq("작성됨").and(user.userId.eq(userId)))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public List<GetMainPostResponse> getHotEventPosts(List<Long> categoryIds) {
		return queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				JPAExpressions
					.select(image.url)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true)))
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.orderBy(post.viewCount.desc())  // 조회수 기준 정렬
			.limit(10) // 상위 10개 게시물만 반환
			.fetch();
	}

	@Override
	public List<GetMainPostResponse> getLatestEventPosts(List<Long> categoryIds) {
		return queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				JPAExpressions
					.select(image.url)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true)))
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.orderBy(post.createdAt.desc()) // 조회수 기준 정렬
			.limit(10) // 상위 10개 게시물 제한
			.fetch();
	}

	@Override
	public List<GetMainPostResponse> getDeadlineEventPosts(List<Long> categoryIds) {
		return queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				JPAExpressions
					.select(image.url)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true)))
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.where(status.name.eq("작성됨")
				.and(post.category.categoryId.in(categoryIds))
				.and(post.event.endTime.after(LocalDateTime.now())) // 현재 이후의 이벤트
				.and(post.event.endTime.before(LocalDateTime.now().plusMonths(1)))  // 한 달 이내
			)
			.orderBy(post.event.endTime.asc())
			.limit(10) // 상위 10개 게시물 제한
			.fetch();
	}

	@Override
	public List<GetRecommendPostResponse> getRecommendationEventPosts(List<Long> categoryIds) {
		return queryFactory
			.select(Projections.constructor(
				GetRecommendPostResponse.class,
				post.postId,
				post.title,
				post.writer,
				post.recommendationCount,
				post.viewCount,
				post.createdAt,
				JPAExpressions
					.select(image.url)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true)))
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.orderBy(post.recommendationCount.desc())
			.limit(10)  // 상위 10권으로 결과 제한
			.fetch();
	}

	@Override
	public List<GetRecommendPostResponse> getTrendingEventPosts(List<Long> categoryIds) {
		return queryFactory
			.select(Projections.constructor(
				GetRecommendPostResponse.class,
				post.postId,
				post.title,
				post.writer,
				post.recommendationCount,
				postView.count(),
				post.createdAt,
				JPAExpressions
					.select(image.url)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true)))
			))
			.from(postView)
			.join(postView.post, post)
			.join(post.category, category)
			.join(post.status, status)
			.where(status.name.eq("작성됨")
				.and(postView.createdAt.gt(LocalDateTime.now().minusDays(7)))
				.and(post.category.categoryId.in(categoryIds)))
			.groupBy(postView.post.postId)
			.orderBy(postView.count().desc())
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
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.orderBy(post.viewCount.desc())  // 조회수 기준 정렬
			.limit(10) // 상위 10개 게시물만 반환
			.fetch();
	}

	@Override
	public Page<GetPostsByCategoryNameResponse> getPostsByEventCategory(Pageable pageable, List<Long> categoryIds) {
		List<GetPostsByCategoryNameResponse> result = queryFactory
			.select(Projections.constructor(
				GetPostsByCategoryNameResponse.class,
				post.postId,
				post.writer,
				post.writerGrade,
				post.title,
				post.recommendationCount,
				post.viewCount,
				post.createdAt,
				JPAExpressions
					.select(image.url)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true)))
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.join(post.user, user)
			.join(user.grade, grade)
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(post.count())
			.from(post)
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Page<GetPostsByCategoryNameResponse> getPostsByCategoryName(Pageable pageable, List<Long> categoryIds) {
		List<GetPostsByCategoryNameResponse> result = queryFactory
			.select(Projections.constructor(
				GetPostsByCategoryNameResponse.class,
				post.postId,
				post.writer,
				post.writerGrade,
				post.title,
				post.recommendationCount,
				post.viewCount,
				post.createdAt,
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
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.orderBy(post.createdAt.desc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(post.count())
			.from(post)
			.where(status.name.eq("작성됨").and(post.category.categoryId.in(categoryIds)))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
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

	@Override
	public Optional<GetTempPostResponse> getTempPost(Long userId) {
		List<GetTempPostResponse> posts = queryFactory
			.select(Projections.constructor(
				GetTempPostResponse.class,
				post.postId,
				post.status.name))
			.from(post)
			.where(post.user.userId.eq(userId).and(post.status.name.eq("작성중")))
			.orderBy(post.postId.asc())
			.fetch();

		return posts.isEmpty() ? Optional.empty() : Optional.of(posts.getLast());
	}

}
