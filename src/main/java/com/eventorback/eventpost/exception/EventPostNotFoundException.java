package com.eventorback.eventpost.exception;

import com.eventorback.global.exception.NotFoundException;

public class EventPostNotFoundException extends NotFoundException {
	public EventPostNotFoundException() {
		super("이벤트 게시물을 찾을 수 없습니다.");
	}
}
