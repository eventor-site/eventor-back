package com.eventorback.reporttype.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class ReportTypeAlreadyExistsException extends AlreadyExistsException {
	public ReportTypeAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 신고유형 '%s'는 이미 존재 하는 신고유형 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
