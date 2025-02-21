package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class NotFoundException extends GlobalException {
	public NotFoundException(String message) {
		super(ErrorStatus.from(
			message,
			HttpStatus.NOT_FOUND,
			LocalDateTime.now())
		);
	}
}
