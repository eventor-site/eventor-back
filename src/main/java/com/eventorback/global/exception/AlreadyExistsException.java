package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends GlobalException {
	public AlreadyExistsException() {
		super(ErrorStatus.from(
			"이미 존재합니다.",
			HttpStatus.CONFLICT,
			LocalDateTime.now())
		);
	}

	public AlreadyExistsException(String message) {
		super(ErrorStatus.from(
			message,
			HttpStatus.CONFLICT,
			LocalDateTime.now())
		);
	}
}
