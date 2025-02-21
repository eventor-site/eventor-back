package com.eventorback.image.exception;

import com.eventorback.global.exception.ServerException;

public class FileExtensionException extends ServerException {
	public FileExtensionException() {
		super("업로드 불가능한 파일 형식 입니다.");
	}
}
