package com.eventorback.post.domain.dto.request;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record UpdatePostRequest(
	String title,
	String content,
	Boolean isNotification,
	LocalDateTime startTime,
	LocalDateTime endTime) {
}
