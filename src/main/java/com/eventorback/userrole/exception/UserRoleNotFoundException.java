package com.eventorback.userrole.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class UserRoleNotFoundException extends NotFoundException {
	public UserRoleNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 회원역할 '%s'는 존재하지 않는 회원역할 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
