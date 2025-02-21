package com.eventorback.image.exception;

import com.eventorback.global.exception.ServerException;

public class ParserException extends ServerException {
	public ParserException() {
		super("업로드 파일을 파싱하는데 오류가 발생하였습니다.");
	}
}
