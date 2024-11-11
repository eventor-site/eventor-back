package com.eventorback.user.domain.dto.request;

import lombok.Builder;

@Builder
public record SignUpRequest(
	String id,
	String password,
	String name,
	String nickName,
	String email,
	String birth,
	String gender,
	String phone) {
}

