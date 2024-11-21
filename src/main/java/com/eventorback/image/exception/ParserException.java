package com.eventorback.image.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.GlobalException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class ParserException extends GlobalException {
	public ParserException() {
		super(
			ErrorStatus.from(String.format("업로드 파일을 파싱하는데 오류가 발생하였습니다."),
				HttpStatus.INTERNAL_SERVER_ERROR,
				LocalDateTime.now()));
	}
}
