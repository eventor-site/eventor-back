package com.eventorback.image.domain.dto.request;

import java.util.List;

import lombok.Builder;

@Builder
public record DeleteImageRequest(
	Long postId,
	List<Long> imageIds
) {
}
