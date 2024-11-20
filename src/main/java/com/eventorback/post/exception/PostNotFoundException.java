package com.eventorback.post.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class PostNotFoundException extends NotFoundException {
	public PostNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 게시물 '%s'는 존재하지 않는 게시물 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
