package com.eventorback.userstop.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class UserStopNotFoundException extends NotFoundException {
	public UserStopNotFoundException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 회원정지 '%s'는 존재하지 않는 회원정지 입니다.", value),
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
