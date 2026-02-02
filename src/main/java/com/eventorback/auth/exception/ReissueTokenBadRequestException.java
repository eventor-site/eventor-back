package com.eventorback.auth.exception;

import com.eventorback.global.exception.BadRequestException;

public class ReissueTokenBadRequestException extends BadRequestException {
	public ReissueTokenBadRequestException() {
		super("요청에 Refresh-Token 값이 없습니다.");
	}
}
