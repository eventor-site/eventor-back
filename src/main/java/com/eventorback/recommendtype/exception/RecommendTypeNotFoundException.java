package com.eventorback.recommendtype.exception;

import com.eventorback.global.exception.NotFoundException;

public class RecommendTypeNotFoundException extends NotFoundException {
	public RecommendTypeNotFoundException() {
		super("추천 유형을 찾을 수 없습니다.");
	}
}
