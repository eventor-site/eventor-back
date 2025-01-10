package com.eventorback.commentreport.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class CommentReportAlreadyExistsException extends AlreadyExistsException {
	public CommentReportAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 댓글 '%s'는 이미 신고한 댓글 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
