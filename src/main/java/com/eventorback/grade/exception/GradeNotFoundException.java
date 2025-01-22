package com.eventorback.grade.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class GradeNotFoundException extends NotFoundException {
	public GradeNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 등급 '%s'는 존재하지 않는 등급 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
