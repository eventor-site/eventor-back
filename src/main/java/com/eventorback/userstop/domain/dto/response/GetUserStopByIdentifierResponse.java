package com.eventorback.userstop.domain.dto.response;

import lombok.Builder;

@Builder
public record GetUserStopByIdentifierResponse(
	String reportTypeName,
	Long reportCount
) {
}
