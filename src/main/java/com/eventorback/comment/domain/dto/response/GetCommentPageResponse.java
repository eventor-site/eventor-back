package com.eventorback.comment.domain.dto.response;

import lombok.Builder;

@Builder
public record GetCommentPageResponse(
	Long page) {
}
