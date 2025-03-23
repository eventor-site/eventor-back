package com.eventorback.comment.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetCommentByUserIdResponse(
	Long postId,
	Long commentId,
	String writer,
	String content,
	Long recommendationCount,
	Long decommendationCount,
	LocalDateTime createdAt,
	String gradeName
) {

}
