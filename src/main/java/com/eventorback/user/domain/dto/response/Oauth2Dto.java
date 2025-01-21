package com.eventorback.user.domain.dto.response;

import lombok.Builder;

@Builder
public record Oauth2Dto(
	String identifier,
	String oauthId
) {
}
