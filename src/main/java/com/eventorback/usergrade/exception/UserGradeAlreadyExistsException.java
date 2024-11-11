package com.eventorback.usergrade.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.sikyeojoback.global.exception.AlreadyExistsException;
import com.sikyeojoback.global.exception.payload.ErrorStatus;

public class UserGradeAlreadyExistsException extends AlreadyExistsException {
	public UserGradeAlreadyExistsException(Object value) {
		super(
			ErrorStatus.from(String.format("해당 회원등급 '%s'는 이미 존재 하는 회원등급 입니다.", value),
				HttpStatus.CONFLICT,
				LocalDateTime.now()));
	}
}
