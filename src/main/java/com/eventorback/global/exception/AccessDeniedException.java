package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.sikyeojoback.global.exception.payload.ErrorStatus;

public class AccessDeniedException extends GlobalException {
	public AccessDeniedException(Long currentUserId, Long requestUserId) {
		super(ErrorStatus.from(
			String.format("현재 사용자 '%d'는 해당 사용자 '%d'의 정보에 대해 접근 권한이 없습니다.", currentUserId, requestUserId),
			HttpStatus.FORBIDDEN,
			LocalDateTime.now()
		));
	}
}
