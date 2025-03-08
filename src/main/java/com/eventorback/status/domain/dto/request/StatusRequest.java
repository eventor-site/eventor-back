package com.eventorback.status.domain.dto.request;

import lombok.Builder;

@Builder
public record StatusRequest(
	String name,
	Long statusTypeId
) {
}
