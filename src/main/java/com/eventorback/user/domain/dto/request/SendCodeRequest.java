package com.eventorback.user.domain.dto.request;

import lombok.Builder;

@Builder
public record SendCodeRequest(
	String email
) {
}
