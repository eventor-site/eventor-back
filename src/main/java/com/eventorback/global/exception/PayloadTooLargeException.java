package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class PayloadTooLargeException extends GlobalException {
	public PayloadTooLargeException() {
		super(ErrorStatus.from(
			"허용된 용량을 초과했습니다.",
			HttpStatus.PAYLOAD_TOO_LARGE,
			LocalDateTime.now())
		);
	}

	public PayloadTooLargeException(String message) {
		super(ErrorStatus.from(
			message,
			HttpStatus.PAYLOAD_TOO_LARGE,
			LocalDateTime.now()
		));
	}
}
