package com.eventorback.global.exception;


import com.sikyeojoback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class NotFoundException extends GlobalException {
	public NotFoundException(ErrorStatus errorStatus) {
		super(errorStatus);
	}
}
