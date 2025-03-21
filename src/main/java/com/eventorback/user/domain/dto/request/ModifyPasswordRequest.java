package com.eventorback.user.domain.dto.request;

import lombok.Builder;

@Builder
public record ModifyPasswordRequest(
	String currentPassword,
	String password
) {
}
