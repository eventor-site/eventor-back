package com.eventorback.user.domain.dto.request;


import lombok.Builder;

@Builder
public record SignUpRequest(
	String identifier,
	String password,
	String name,
	String nickname,
	String email,
	String birth,
	String gender,
	String phone,
	String oauthId,
	String oauthType,
	String certifyCode) {

	/** 소셜 로그인 값(oauthId, oauthType)을 비운 복사본 */
	public SignUpRequest withoutOauth() {
		return new SignUpRequest(identifier, password, name, nickname, email, birth, gender, phone, null, null,
			certifyCode);
	}
}

