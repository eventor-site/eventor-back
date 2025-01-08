package com.eventorback.bookmark.domain.dto.request;

import lombok.Builder;

@Builder
public record CreateBookmarkRequest(
	Long categoryId) {
}
