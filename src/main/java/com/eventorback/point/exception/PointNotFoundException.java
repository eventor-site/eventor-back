package com.eventorback.point.exception;

import com.eventorback.global.exception.NotFoundException;

public class PointNotFoundException extends NotFoundException {
	public PointNotFoundException() {
		super("포인트를 찾을 수 없습니다.");
	}
}
