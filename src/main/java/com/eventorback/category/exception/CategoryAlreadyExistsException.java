package com.eventorback.category.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
	public CategoryAlreadyExistsException() {
		super("이미 존재 하는 카테고리 입니다.");
	}
}
