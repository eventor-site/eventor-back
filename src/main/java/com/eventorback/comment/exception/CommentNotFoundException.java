package com.eventorback.comment.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class CommentNotFoundException extends NotFoundException {
	public CommentNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 댓글 '%s'는 존재하지 않는 댓글 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
