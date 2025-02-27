package com.eventorback.user.exception;

import com.eventorback.global.exception.ForbiddenException;

public class UserActiveStopException extends ForbiddenException {
	public UserActiveStopException() {
		super("현재 사용자는 정지 계정입니다.");
	}
}
