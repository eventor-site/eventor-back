package com.eventorback.statustype.exception;

import com.eventorback.global.exception.AlreadyExistsException;

public class StatusTypeAlreadyExistsException extends AlreadyExistsException {
	public StatusTypeAlreadyExistsException() {
		super("상태 유형이 이미 존재 합니다.");
	}
}
