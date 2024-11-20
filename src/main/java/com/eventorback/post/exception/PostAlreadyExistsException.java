package com.eventorback.post.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class PostAlreadyExistsException extends AlreadyExistsException {
	public PostAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 게시물 '%s'는 이미 존재 하는 게시물 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
