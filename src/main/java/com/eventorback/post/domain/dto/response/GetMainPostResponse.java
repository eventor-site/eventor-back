package com.eventorback.post.domain.dto.response;

import lombok.Builder;

@Builder
public record GetMainPostResponse(
	Long postId,
	String title,
	String imageUrl) {
}