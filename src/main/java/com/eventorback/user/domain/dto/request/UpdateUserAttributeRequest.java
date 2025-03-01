package com.eventorback.user.domain.dto.request;

import lombok.Builder;

@Builder
public record UpdateUserAttributeRequest(
	Long statusId,
	Long gradeId
) {
}
