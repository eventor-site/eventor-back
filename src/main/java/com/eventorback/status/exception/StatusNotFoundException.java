package com.eventorback.status.exception;

import com.eventorback.global.exception.NotFoundException;

public class StatusNotFoundException extends NotFoundException {
	public StatusNotFoundException() {
		super("상태를 찾을 수 없습니다.");
	}
}
