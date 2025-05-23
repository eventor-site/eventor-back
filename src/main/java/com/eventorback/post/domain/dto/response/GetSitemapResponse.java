package com.eventorback.post.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetSitemapResponse(
	Long postId,
	LocalDateTime createdAt
) {
}
