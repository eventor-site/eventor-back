package com.eventorback.image.exception;

import com.eventorback.global.exception.ServerException;

public class CreateFolderException extends ServerException {
	public CreateFolderException(String message) {
		super("폴더 생성 실패: " + message);
	}
}
