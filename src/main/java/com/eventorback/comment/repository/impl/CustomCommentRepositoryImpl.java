package com.eventorback.comment.repository.impl;

import static com.eventorback.comment.domain.entity.QComment.*;

import java.util.List;
import java.util.Optional;

import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.repository.CustomCommentRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetCommentResponse> getCommentsByPostId(Long postId) {
		return List.of();
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
