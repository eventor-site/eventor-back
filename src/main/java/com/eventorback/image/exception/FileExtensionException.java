package com.eventorback.image.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.GlobalException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class FileExtensionException extends GlobalException {
	public FileExtensionException() {
		super(
			ErrorStatus.from(String.format("업로드 불가능한 파일 형식 입니다."),
				HttpStatus.INTERNAL_SERVER_ERROR,
				LocalDateTime.now()));
	}
}
