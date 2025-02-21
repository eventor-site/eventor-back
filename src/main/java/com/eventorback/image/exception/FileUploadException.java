package com.eventorback.image.exception;

import com.eventorback.global.exception.ServerException;

public class FileUploadException extends ServerException {
	public FileUploadException() {
		super("파일 업로드를 실패 하였습니다.");
	}
}
