package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

public class AccessDeniedException extends GlobalException {
	public AccessDeniedException() {
		super(ErrorStatus.from(
			String.format("현재 사용자는 정보에 대해 접근 권한이 없습니다."),
			HttpStatus.FORBIDDEN,
			LocalDateTime.now()
		));
	}
}
