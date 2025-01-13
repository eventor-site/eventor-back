package com.eventorback.user.domain.dto.response;

import lombok.Builder;

@Builder
public record GetUserByIdentifier(
	String identifier) {
}
