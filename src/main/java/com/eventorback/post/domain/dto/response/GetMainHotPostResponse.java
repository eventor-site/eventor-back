package com.eventorback.post.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetMainHotPostResponse(
	Long postId,
	String title,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String imageUrl,
	String imageType) {
}