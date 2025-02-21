package com.eventorback.image.exception;

import com.eventorback.global.exception.NotFoundException;

public class ImageNotFoundException extends NotFoundException {
	public ImageNotFoundException() {
		super("이미지를 찾을 수 없습니다.");
	}
}
