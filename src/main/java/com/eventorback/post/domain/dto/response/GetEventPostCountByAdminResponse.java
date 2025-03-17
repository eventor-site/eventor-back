package com.eventorback.post.domain.dto.response;

import lombok.Builder;

@Builder
public record GetEventPostCountByAdminResponse(
	String nickname,
	Long eventPostCount) {
}