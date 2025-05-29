package com.eventorback.tour.exception;

import com.eventorback.global.exception.BadRequestException;

public class AddressBadRequestException extends BadRequestException {
	public AddressBadRequestException() {
		super("올바른 주소가 아닙니다.");
	}
}
