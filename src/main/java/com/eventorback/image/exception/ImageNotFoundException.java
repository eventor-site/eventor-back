package com.eventorback.image.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class ImageNotFoundException extends NotFoundException {
	public ImageNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 이미지 '%s'는 존재하지 않는 이미지 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
