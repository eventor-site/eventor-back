package com.eventorback.grade.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class GradeAlreadyExistsException extends AlreadyExistsException {
	public GradeAlreadyExistsException() {
		super("등급이 이미 존재합니다.");
	}
}
