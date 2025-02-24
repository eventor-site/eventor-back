package com.eventorback.post.domain.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record CreatePostRequest(
	String categoryName,
	String title,
	String content,
	LocalDateTime startTime,
	LocalDateTime endTime) {
}
