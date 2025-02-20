package com.eventorback.point.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class PointNotFoundException extends NotFoundException {
	public PointNotFoundException() {
		super(
			ErrorStatus.from(String.format("존재 하지 않는 포인트 입니다."),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
