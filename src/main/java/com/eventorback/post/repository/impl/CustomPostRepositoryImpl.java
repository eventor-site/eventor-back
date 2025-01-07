package com.eventorback.post.repository.impl;

import static com.eventorback.category.domain.entity.QCategory.*;
import static com.eventorback.image.domain.entity.QImage.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.status.domain.entity.QStatus.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.repository.CustomPostRepository;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {
	private final JPAQueryFactory queryFactory;

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
			.where(status.name.eq("작성됨"))
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
				image.url.min() // 그룹화된 이미지 중 가장 작은 ID의 URL 가져오기
			))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(image).on(image.post.postId.eq(post.postId))
			.where(status.name.eq("작성됨").and(category.name.eq("이벤트")))
			.groupBy(post.postId, post.title) // 게시물별 그룹화
			.orderBy(post.viewCount.desc()) // 조회수 기준 정렬
			.limit(10) // 상위 10개 게시물 제한
			.fetch();
	}

	@Override
	public List<GetMainPostResponse> getLatestEventPosts() {
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
			.on(image.post.postId.eq(post.postId)) // 게시물과 연결된 이미지
			.where(status.name.eq("작성됨").and(category.name.eq("이벤트")))
			.orderBy(post.createdAt.desc())
			.limit(10)  // 상위 10권으로 결과 제한
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
			.on(image.post.postId.eq(post.postId)) // 게시물과 연결된 이미지
			.where(status.name.eq("작성됨").and(category.name.eq("이벤트")))
			.orderBy(post.recommendationCount.desc())
			.limit(10)  // 상위 10권으로 결과 제한
			.fetch();
	}

	@Override
	public List<GetPostSimpleResponse> getPostsByCategoryName(CurrentUserDto currentUser, String categoryName) {
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
			.where(status.name.eq("작성됨").and(category.name.eq(categoryName)))
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
