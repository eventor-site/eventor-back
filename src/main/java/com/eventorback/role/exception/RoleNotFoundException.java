package com.eventorback.role.exception;

import com.eventorback.global.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
	public RoleNotFoundException() {
		super("역할을 찾을 수 없습니다.");
	}
}
