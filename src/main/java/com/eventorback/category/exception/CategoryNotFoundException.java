package com.eventorback.category.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.sikyeojoback.global.exception.NotFoundException;
import com.sikyeojoback.global.exception.payload.ErrorStatus;

public class CategoryNotFoundException extends NotFoundException {
	public CategoryNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 카테고리 '%s'는 존재하지 않는 카테고리 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
