package com.eventorback.comment.domain.dto.response;

import java.time.LocalDateTime;

import com.eventorback.comment.domain.entity.Comment;

import lombok.Builder;

@Builder
public record GetCommentByUserIdResponse(
	Long postId,
	Long commentId,
	String writer,
	String content,
	Long recommendationCount,
	Long decommendationCount,
	LocalDateTime createdAt) {

	public static GetCommentByUserIdResponse fromEntity(Comment comment) {
		return GetCommentByUserIdResponse.builder()
			.postId(comment.getPost().getPostId())
			.commentId(comment.getCommentId())
			.writer(comment.getWriter())
			.content(comment.getContent())
			.recommendationCount(comment.getRecommendationCount())
			.decommendationCount(comment.getDecommendationCount())
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
