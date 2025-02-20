package com.eventorback.point.domain.dto.response;

import lombok.Builder;

@Builder
public record GetPointResponse(
	Long pointId,
	String name,
	Long amount
) {
}
