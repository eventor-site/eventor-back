package com.eventorback.postview.repository.impl;

import static com.eventorback.image.domain.entity.QImage.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.postview.domain.entity.QPostView.*;
import static com.eventorback.status.domain.entity.QStatus.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.postview.repository.CustomPostViewRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPostViewRepositoryImpl implements CustomPostViewRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetMainPostResponse> getPostViewsByViewerId(String viewerId, Pageable pageable) {
		List<GetMainPostResponse> result = queryFactory
			.select(Projections.constructor(
				GetMainPostResponse.class,
				post.postId,
				post.title,
				JPAExpressions
					.select(image.url)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true))),
				JPAExpressions
					.select(image.type)
					.from(image)
					.where(image.post.postId.eq(post.postId).and(image.isThumbnail.eq(true)))
			))
			.from(postView)
			.join(postView.post, post)
			.join(post.status, status)
			.where(status.name.eq("작성됨").and(postView.viewerId.eq(viewerId)))
			.orderBy(postView.viewedAt.desc())
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(postView.count())
			.from(postView)
			.join(postView.post, post)
			.join(post.status, status)
			.where(status.name.eq("작성됨").and(postView.viewerId.eq(viewerId)))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}
}
