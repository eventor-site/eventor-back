package com.eventorback.userstop.exception;

import com.eventorback.global.exception.NotFoundException;

public class UserStopNotFoundException extends NotFoundException {
	public UserStopNotFoundException() {
		super("회원 정지를 찾을 수 없습니다.");
	}
}
