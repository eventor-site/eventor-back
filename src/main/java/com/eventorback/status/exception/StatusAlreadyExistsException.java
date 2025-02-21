package com.eventorback.status.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class StatusAlreadyExistsException extends AlreadyExistsException {
	public StatusAlreadyExistsException() {
		super("상태가 이미 존재합니다.");
	}
}
