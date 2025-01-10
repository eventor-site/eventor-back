package com.eventorback.comment.repository.impl;

import static com.eventorback.comment.domain.entity.QComment.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.repository.CustomCommentRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	// TODO: DB 구조 변경 예정 https://annahxxl.tistory.com/5
	public List<GetCommentResponse> getCommentsByPostId(Long postId) {
		return queryFactory
			.select(Projections.constructor(
				GetCommentResponse.class,
				comment.commentId,
				comment.parentComment.commentId,
				comment.writer,
				comment.content,
				comment.recommendationCount,
				comment.decommendationCount,
				JPAExpressions
					.select(Projections.constructor(GetCommentResponse.class,
						comment.commentId,
						comment.parentComment.commentId,
						comment.writer,
						comment.content,
						comment.recommendationCount,
						comment.decommendationCount,
						comment.createdAt))
					.from(comment)
					.where(comment.parentComment.commentId.eq(comment.commentId)),
				comment.createdAt))
			.from(comment)
			.join(comment.parentComment, comment)
			.join(comment.post, post)
			.where(post.postId.eq(postId))
			.orderBy(comment.createdAt.asc())
			.fetch();
	}

	@Override
	public List<GetCommentByUserIdResponse> getCommentsByUserId(Long userId) {
		return queryFactory
			.select(Projections.constructor(
				GetCommentByUserIdResponse.class,
				comment.post.postId,
				comment.commentId,
				comment.writer,
				comment.content,
				comment.recommendationCount,
				comment.decommendationCount,
				comment.createdAt))
			.from(comment)
			.where(user.userId.eq(userId))
			.orderBy(comment.createdAt.desc())
			.fetch();
	}

	@Override
	public Optional<Comment> getComment(Long commentId) {
		return Optional.ofNullable(
			queryFactory
				.selectFrom(comment)
				.where(comment.commentId.eq(commentId))
				.fetchOne()
		);
	}
}
