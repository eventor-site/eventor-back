package com.eventorback.reporttype.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class ReportTypeNotFoundException extends NotFoundException {
	public ReportTypeNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 신고유형 '%s'는 존재하지 않는 신고유형 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
