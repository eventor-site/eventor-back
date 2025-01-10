package com.eventorback.postreport.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class PostReportAlreadyExistsException extends AlreadyExistsException {
	public PostReportAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 게시물 '%s'는 이미 신고한 게시물 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
