package com.eventorback.grade.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class GradeAlreadyExistsException extends AlreadyExistsException {
	public GradeAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 등급 '%s'는 이미 존재 하는 등급 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
