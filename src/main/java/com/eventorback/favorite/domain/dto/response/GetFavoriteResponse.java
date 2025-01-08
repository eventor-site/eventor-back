package com.eventorback.favorite.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetFavoriteResponse(
	Long favoriteId,
	Long postId,
	String writer,
	String title,
	LocalDateTime createdAt) {
}
