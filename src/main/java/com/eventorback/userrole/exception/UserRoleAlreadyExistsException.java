package com.eventorback.userrole.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class UserRoleAlreadyExistsException extends AlreadyExistsException {
	public UserRoleAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 회원역할 '%s'는 이미 존재 하는 회원역할 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
