package com.eventorback.image.exception;

import com.eventorback.global.exception.PayloadTooLargeException;

public class ImageCapacityExceededException extends PayloadTooLargeException {
	public ImageCapacityExceededException() {
		super("이미지가 허용된 용량을 초과 했습니다.");
	}
}
