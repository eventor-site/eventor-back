package com.eventorback.post.domain.dto.request;

import lombok.Builder;

@Builder
public record UpdatePostRequest(
	String title,
	String content,
	Boolean isNotification) {
}
