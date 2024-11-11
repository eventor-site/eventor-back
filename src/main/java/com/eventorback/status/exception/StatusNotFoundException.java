package com.eventorback.status.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class StatusNotFoundException extends NotFoundException {
	public StatusNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 상태 '%s'는 존재하지 않는 상태 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
