package com.eventorback.post.domain.dto.request;

import lombok.Builder;

@Builder
public record CreatePostRequest(
	String categoryName,
	String title,
	String content,
	Boolean isNotification) {
}
