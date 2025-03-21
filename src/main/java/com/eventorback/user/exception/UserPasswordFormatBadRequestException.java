package com.eventorback.user.exception;

import com.eventorback.global.exception.BadRequestException;

public class UserPasswordFormatBadRequestException extends BadRequestException {
	public UserPasswordFormatBadRequestException() {
		super("비밀번호는 최소 8자 이상, 영문, 숫자, 특수문자를 포함해야 합니다.");
	}
}
