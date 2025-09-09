package com.eventorback.user.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class UserAlreadyExistsException extends AlreadyExistsException {
	public UserAlreadyExistsException() {
		super("이미 가입한 계정 입니다.");
	}
}
