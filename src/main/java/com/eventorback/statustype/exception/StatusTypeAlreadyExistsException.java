package com.eventorback.statustype.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.sikyeojoback.global.exception.AlreadyExistsException;
import com.sikyeojoback.global.exception.payload.ErrorStatus;

public class StatusTypeAlreadyExistsException extends AlreadyExistsException {
	public StatusTypeAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 상태유형 '%s'는 이미 존재 하는 상태유형 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
