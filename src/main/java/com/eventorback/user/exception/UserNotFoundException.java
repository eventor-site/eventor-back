package com.eventorback.user.exception;

import com.eventorback.global.exception.NotFoundException;

public class UserNotFoundException extends NotFoundException {
	public UserNotFoundException() {
		super("회원을 찾을 수 없습니다.");
	}
}
