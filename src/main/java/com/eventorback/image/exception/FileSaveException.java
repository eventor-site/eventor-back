package com.eventorback.image.exception;

import com.eventorback.global.exception.ServerException;

public class FileSaveException extends ServerException {
	public FileSaveException(String message) {
		super("파일 저장 실패: " + message);
	}
}
