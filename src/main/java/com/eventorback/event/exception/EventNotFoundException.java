package com.eventorback.event.exception;

import com.eventorback.global.exception.NotFoundException;

public class EventNotFoundException extends NotFoundException {
	public EventNotFoundException() {
		super("이벤트를 찾을 수 없습니다.");
	}
}
