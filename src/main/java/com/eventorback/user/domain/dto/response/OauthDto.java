package com.eventorback.user.domain.dto.response;

import lombok.Builder;

@Builder
public record OauthDto(
	String oauthId,
	String oauthType
) {
}
