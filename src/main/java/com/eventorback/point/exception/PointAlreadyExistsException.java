package com.eventorback.point.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class PointAlreadyExistsException extends AlreadyExistsException {
	public PointAlreadyExistsException() {
		super("포인트가 이미 존재합니다.");
	}
}
