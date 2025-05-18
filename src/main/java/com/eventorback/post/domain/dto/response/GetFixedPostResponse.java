package com.eventorback.post.domain.dto.response;

import lombok.Builder;

@Builder
public record GetFixedPostResponse(
	Long postId,
	String title) {
}