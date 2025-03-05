package com.eventorback.user.exception;

import com.eventorback.global.exception.BadRequestException;

public class NicknameBadRequestException extends BadRequestException {
	public NicknameBadRequestException() {
		super("사용할 수 없는 닉네임 입니다.");
	}
}
