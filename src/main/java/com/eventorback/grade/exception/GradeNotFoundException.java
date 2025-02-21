package com.eventorback.grade.exception;

import com.eventorback.global.exception.NotFoundException;

public class GradeNotFoundException extends NotFoundException {
	public GradeNotFoundException() {
		super("등급을 찾을 수 없습니다.");
	}
}
