package com.eventorback.recommendtype.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class RecommendTypeAlreadyExistsException extends AlreadyExistsException {
	public RecommendTypeAlreadyExistsException() {
		super("추천 유형이 이미 존재합니다.");
	}
}
