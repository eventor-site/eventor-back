package com.eventorback.role.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class RoleAlreadyExistsException extends AlreadyExistsException {
	public RoleAlreadyExistsException() {
		super("권한이 이미 존재 합니다.");
	}
}
