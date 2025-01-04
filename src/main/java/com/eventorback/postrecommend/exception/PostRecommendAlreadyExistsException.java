package com.eventorback.postrecommend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class PostRecommendAlreadyExistsException extends AlreadyExistsException {
	public PostRecommendAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 추천 게시물 '%s'는 이미 존재 하는 추천 게시물 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
