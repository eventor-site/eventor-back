package com.eventorback.userrole.exception;

import com.eventorback.global.exception.NotFoundException;

public class UserRoleNotFoundException extends NotFoundException {
	public UserRoleNotFoundException() {
		super("회원 역할을 찾을 수 없습니다.");
	}
}
