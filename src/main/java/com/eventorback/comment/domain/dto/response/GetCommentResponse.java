package com.eventorback.comment.domain.dto.response;

import java.time.LocalDateTime;

import com.eventorback.comment.domain.entity.Comment;

import lombok.Builder;

@Builder
public record GetCommentResponse(
	Long commentId,
	Long parentCommentId,
	String writer,
	String content,
	Long recommendationCount,
	LocalDateTime createdAt) {
	public static GetCommentResponse fromEntity(Comment comment) {
		Long parentCommentId =
			comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null;

		return GetCommentResponse.builder()
			.commentId(comment.getCommentId())
			.parentCommentId(parentCommentId)
			.writer(comment.getWriter())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.recommendationCount(comment.getRecommendationCount())
			.build();
	}
}
