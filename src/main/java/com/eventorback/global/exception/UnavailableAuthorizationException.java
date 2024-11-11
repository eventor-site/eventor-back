package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.sikyeojoback.global.exception.payload.ErrorStatus;

public class UnavailableAuthorizationException extends GlobalException {
	public UnavailableAuthorizationException() {
		super(ErrorStatus.from(
			"해당 요청에 대한 권한이 없습니다.",
			HttpStatus.FORBIDDEN,
			LocalDateTime.now()
		));
	}
}
