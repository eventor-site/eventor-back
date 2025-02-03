package com.eventorback.userstop.domain.dto.response;

import lombok.Builder;

@Builder
public record GetUserStopByUserIdResponse(
	String reportTypeName,
	Long reportCount
) {
}
