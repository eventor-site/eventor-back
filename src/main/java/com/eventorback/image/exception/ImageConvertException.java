package com.eventorback.image.exception;

import com.eventorback.global.exception.ServerException;

import lombok.Getter;

@Getter
public class ImageConvertException extends ServerException {
	public ImageConvertException() {
		super("이미지 변환에 실패 하였습니다.");
	}

	public ImageConvertException(String message) {
		super(message);
	}
}
