package com.eventorback.category.exception;

import com.eventorback.global.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
	public CategoryNotFoundException() {
		super("카테고리를 찾을 수 없습니다.");
	}
}
