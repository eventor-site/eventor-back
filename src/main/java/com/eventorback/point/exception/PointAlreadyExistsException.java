package com.eventorback.point.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.AlreadyExistsException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class PointAlreadyExistsException extends AlreadyExistsException {
	public PointAlreadyExistsException() {
		super(
			ErrorStatus.from(String.format("이미 존재 하는 포인트 입니다."),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
