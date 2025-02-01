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
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetCommentByUserIdResponse> getComments() {
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
			.where(comment.status.name.eq("작성됨"))
			.orderBy(comment.createdAt.desc())
			.fetch();
	}

	@Override
	public List<GetCommentResponse> getCommentsByPostId(CurrentUserDto currentUser, Long postId) {
		BooleanExpression isAdmin = Expressions.FALSE;
		BooleanExpression isOwner = Expressions.FALSE;

		// currentUser가 null이 아닐 때만 조건을 설정
		if (currentUser != null) {
			isAdmin = Expressions.asBoolean(currentUser.roles().contains("admin")).isTrue();
			isOwner = comment.user.userId.eq(currentUser.userId());
		}

		return queryFactory
			.select(Projections.constructor(
				GetCommentResponse.class,
				comment.commentId,
				comment.parentComment.commentId,
				comment.writer,
				new CaseBuilder()
					.when(comment.status.name.eq("삭제됨"))
					.then("[삭제된 댓글입니다.]")
					.otherwise(comment.content),
				comment.recommendationCount,
				comment.decommendationCount,
				comment.createdAt,
				comment.user.grade.name,
				new CaseBuilder()
					.when(isOwner.or(isAdmin))
					.then(true)
					.otherwise(false),
				comment.depth)
			)
			.from(comment)
			.join(comment.post, post)
			.where(post.postId.eq(postId))
			.orderBy(comment.group.asc(), comment.groupOrder.asc())
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
			.where(user.userId.eq(userId).and(comment.status.name.eq("작성됨")))
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

	@Override
	public Long getMaxGroup() {
		Long maxGroup = queryFactory
			.select(comment.group.max())
			.from(comment)
			.fetchOne();
		return maxGroup != null ? maxGroup : 1L;
	}

	@Override
	public Long getTotalChildCount(Long group) {
		return queryFactory
			.select(comment.childCount.sum())
			.from(comment)
			.where(comment.group.eq(group))
			.fetchOne();
	}

	@Override
	public List<Comment> getGreaterGroupOrder(Long group, Long groupOrder) {
		return queryFactory
			.select(comment)
			.from(comment)
			.where(comment.group.eq(group), comment.groupOrder.goe(groupOrder))    //greater than
			.fetch();
	}
}
