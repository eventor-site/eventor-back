package com.eventorback.role.exception;

import com.eventorback.global.exception.NotFoundException;

public class RoleNotFoundException extends NotFoundException {
	public RoleNotFoundException() {
		super("권한을 찾을 수 없습니다.");
	}
}
