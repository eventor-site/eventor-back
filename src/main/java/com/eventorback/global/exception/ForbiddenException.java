package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

public class ForbiddenException extends GlobalException {
	public ForbiddenException(String message) {
		super(ErrorStatus.from(
			message,
			HttpStatus.FORBIDDEN,
			LocalDateTime.now()
		));
	}
}
