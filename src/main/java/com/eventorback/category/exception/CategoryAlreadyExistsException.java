package com.eventorback.category.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
	public CategoryAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 카테고리 '%s'는 이미 존재 하는 카테고리 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
