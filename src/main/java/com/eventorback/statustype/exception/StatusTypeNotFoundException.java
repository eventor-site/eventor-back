package com.eventorback.statustype.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class StatusTypeNotFoundException extends NotFoundException {
	public StatusTypeNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 상태유형 '%s'는 존재하지 않는 상태유형 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
