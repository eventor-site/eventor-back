package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends GlobalException {
	public AlreadyExistsException(String message) {
		super(ErrorStatus.from(
			message,
			HttpStatus.CONFLICT,
			LocalDateTime.now())
		);
	}
}
