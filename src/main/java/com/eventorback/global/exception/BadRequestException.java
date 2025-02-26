package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class BadRequestException extends GlobalException {
	public BadRequestException() {
		super(ErrorStatus.from(
			"잘못된 요청입니다.",
			HttpStatus.BAD_REQUEST,
			LocalDateTime.now())
		);
	}

	public BadRequestException(String message) {
		super(ErrorStatus.from(
			message,
			HttpStatus.BAD_REQUEST,
			LocalDateTime.now())
		);
	}
}
