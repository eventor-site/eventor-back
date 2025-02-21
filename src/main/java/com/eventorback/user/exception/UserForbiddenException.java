package com.eventorback.user.exception;

import com.eventorback.global.exception.ForbiddenException;

public class UserForbiddenException extends ForbiddenException {
	public UserForbiddenException() {
		super("현재 사용자는 접근이 금지 되었습니다.");
	}
}
