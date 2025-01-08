package com.eventorback.bookmark.domain.dto.response;

import lombok.Builder;

@Builder
public record GetBookmarkResponse(
	Long bookmarkId,
	String categoryName) {
}
