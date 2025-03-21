package com.eventorback.user.domain.dto.request;

import lombok.Builder;

@Builder
public record CertifyEmailRequest(
	String email,
	String type,
	String certifyCode
) {
}
