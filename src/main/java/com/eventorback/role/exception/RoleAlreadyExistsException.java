package com.eventorback.role.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.sikyeojoback.global.exception.AlreadyExistsException;
import com.sikyeojoback.global.exception.payload.ErrorStatus;

public class RoleAlreadyExistsException extends AlreadyExistsException {
	public RoleAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 역할 '%s'는 이미 존재 하는 역할 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
