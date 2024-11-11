package com.eventorback.status.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class StatusAlreadyExistsException extends AlreadyExistsException {
	public StatusAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 상태 '%s'는 이미 존재 하는 상태 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
