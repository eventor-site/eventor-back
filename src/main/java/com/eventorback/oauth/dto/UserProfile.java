package com.eventorback.oauth.dto;

import lombok.Builder;

@Builder
public record UserProfile(
	String oauthId,
	String oauthType,
	String email,
	String name,
	String birth,
	String gender,
	String phone
) {
}
