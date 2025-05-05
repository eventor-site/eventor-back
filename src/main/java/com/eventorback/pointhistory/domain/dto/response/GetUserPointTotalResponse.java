package com.eventorback.pointhistory.domain.dto.response;

import lombok.Builder;

@Builder
public record GetUserPointTotalResponse(
	Long userId,
	String nickname,
	String email,
	Long amount
) {
}
