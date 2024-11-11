package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.sikyeojoback.global.exception.payload.ErrorStatus;

public class UserNotActiveException extends GlobalException {
	public UserNotActiveException() {
		super(ErrorStatus.from(
			"현재 사용자는 휴면 계정입니다.",
			HttpStatus.FORBIDDEN,
			LocalDateTime.now()
		));
	}
}
