package com.eventorback.role.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class RoleNotFoundException extends NotFoundException {
	public RoleNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 역할 '%s'는 존재하지 않는 역할 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
