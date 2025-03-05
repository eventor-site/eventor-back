package com.eventorback.comment.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetCommentResponse(
	Long commentId,
	Long parentCommentId,
	String writer,
	String writerGrade,
	String content,
	Long recommendationCount,
	Long decommendationCount,
	LocalDateTime createdAt,
	Boolean isAuthorized,
	Long depth) {

}
