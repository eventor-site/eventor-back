package com.eventorback.image.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class ImageNotFoundException extends NotFoundException {
	public ImageNotFoundException() {
		super(
			ErrorStatus.from("이미지를 찾을 수 없습니다.",
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
