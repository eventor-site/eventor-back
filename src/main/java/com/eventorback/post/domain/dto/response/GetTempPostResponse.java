package com.eventorback.post.domain.dto.response;

import lombok.Builder;

@Builder
public record GetTempPostResponse(
	Long postId,
	String statusName
) {
}
