package com.eventorback.image.exception;

import com.eventorback.global.exception.NotFoundException;

public class FileExtentionNotFoundException extends NotFoundException {
	public FileExtentionNotFoundException() {
		super("확장자를 찾을 수 없습니다.");
	}
}
