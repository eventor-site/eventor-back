package com.eventorback.recommendtype.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class RecommendTypeNotFoundException extends NotFoundException {
	public RecommendTypeNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 추천유형 '%s'는 존재하지 않는 추천유형 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
