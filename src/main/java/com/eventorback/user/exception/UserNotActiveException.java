package com.eventorback.user.exception;

import com.eventorback.global.exception.ForbiddenException;

public class UserNotActiveException extends ForbiddenException {
	public UserNotActiveException() {
		super("현재 사용자는 휴면 계정입니다.");
	}
}
