package com.eventorback.favorite.domain.dto.request;

import lombok.Builder;

@Builder
public record CreateFavoriteRequest(
	Long postId) {
}
