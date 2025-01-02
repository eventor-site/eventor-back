package com.eventorback.comment.domain.dto.request;

import lombok.Builder;

@Builder
public record UpdateCommentRequest(
	String content) {
}
