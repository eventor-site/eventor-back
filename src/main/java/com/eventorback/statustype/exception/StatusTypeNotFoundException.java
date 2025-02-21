package com.eventorback.statustype.exception;

import com.eventorback.global.exception.NotFoundException;

public class StatusTypeNotFoundException extends NotFoundException {
	public StatusTypeNotFoundException() {
		super("상태 유형을 찾을 수 없습니다.");
	}
}
