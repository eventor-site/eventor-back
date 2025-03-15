package com.eventorback.post.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetPostsByCategoryNameResponse(
	Long postId,
	String writer,
	String writerGrade,
	String title,
	Long recommendationCount,
	Long viewCount,
	LocalDateTime createdAt,
	String eventStatusName,
	Integer remainingDay,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String imageUrl) {
}
