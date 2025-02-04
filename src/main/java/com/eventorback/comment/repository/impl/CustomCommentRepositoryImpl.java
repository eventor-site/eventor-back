package com.eventorback.comment.repository.impl;

import static com.eventorback.comment.domain.entity.QComment.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.user.domain.entity.QUser.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentPageResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.repository.CustomCommentRepository;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public Page<GetCommentByUserIdResponse> getComments(Pageable pageable) {
		List<GetCommentByUserIdResponse> result = queryFactory
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
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(comment.count())
			.from(comment)
			.where(comment.status.name.eq("작성됨"))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Page<GetCommentResponse> getCommentsByPostId(Pageable pageable, CurrentUserDto currentUser, Long postId) {
		BooleanExpression isAdmin = Expressions.FALSE;
		BooleanExpression isOwner = Expressions.FALSE;

		// currentUser가 null이 아닐 때만 조건을 설정
		if (currentUser != null) {
			isAdmin = Expressions.asBoolean(currentUser.roles().contains("admin")).isTrue();
			isOwner = comment.user.userId.eq(currentUser.userId());
		}

		List<GetCommentResponse> result = queryFactory
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
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(comment.count())
			.from(comment)
			.where(comment.post.postId.eq(postId))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public Page<GetCommentByUserIdResponse> getCommentsByUserId(Pageable pageable, Long userId) {
		List<GetCommentByUserIdResponse> result = queryFactory
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
			.offset(pageable.getOffset()) // 페이지 시작점
			.limit(pageable.getPageSize()) // 페이지 크기
			.fetch();

		Long total = Optional.ofNullable(queryFactory
			.select(comment.count())
			.from(comment)
			.where(user.userId.eq(userId).and(comment.status.name.eq("작성됨")))
			.fetchOne()).orElse(0L);

		return new PageImpl<>(result, pageable, total);
	}

	@Override
	public GetCommentPageResponse getComment(Long postId, Long commentId) {
		// 특정 댓글의 group과 groupOrder 조회
		Tuple commentTuple = queryFactory
			.select(comment.group, comment.groupOrder)
			.from(comment)
			.where(comment.commentId.eq(commentId))
			.fetchOne();

		if (commentTuple == null) {
			return new GetCommentPageResponse(1L); // 댓글이 존재하지 않으면 첫 페이지로 반환
		}

		Long groupValue = commentTuple.get(comment.group);
		Long groupOrderValue = commentTuple.get(comment.groupOrder);

		// 특정 댓글보다 앞에 있는 댓글 개수 카운트
		Long commentIndex = queryFactory
			.select(Wildcard.count)
			.from(comment)
			.where(
				comment.post.postId.eq(postId),
				// (1) group이 작은 경우 OR (2) 같은 group에서 groupOrder가 작은 경우
				comment.group.lt(groupValue)
					.or(comment.group.eq(groupValue).and(comment.groupOrder.lt(groupOrderValue)))
			)
			.fetchOne();

		return new GetCommentPageResponse(commentIndex / 10L + 1L);
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
