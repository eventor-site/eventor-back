package com.eventorback.comment.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.eventorback.comment.domain.entity.Comment;

import lombok.Builder;

@Builder
public record GetCommentResponse(
	Long commentId,
	Long parentCommentId,
	String writer,
	String content,
	Long recommendationCount,
	Long decommendationCount,
	List<GetCommentResponse> childComments,
	LocalDateTime createdAt) {
	public static GetCommentResponse fromEntity(Comment comment) {
		Long parentCommentId =
			comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null;

		List<GetCommentResponse> childComments = comment.getChildrenComments() != null ?
			comment.getChildrenComments().stream().map(GetCommentResponse::fromEntity).toList() : null;

		return GetCommentResponse.builder()
			.commentId(comment.getCommentId())
			.parentCommentId(parentCommentId)
			.writer(comment.getWriter())
			.content(comment.getContent())
			.recommendationCount(comment.getRecommendationCount())
			.decommendationCount(comment.getDecommendationCount())
			.childComments(childComments)
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
