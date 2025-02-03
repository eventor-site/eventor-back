package com.eventorback.status.domain.dto.response;

import lombok.Builder;

@Builder
public record GetStatusResponse(
	Long statusId,
	String name,
	String statusTypeName) {
}
