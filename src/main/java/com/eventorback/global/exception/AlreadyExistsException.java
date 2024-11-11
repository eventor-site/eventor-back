package com.eventorback.global.exception;


import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class AlreadyExistsException extends GlobalException {
	public AlreadyExistsException(ErrorStatus errorStatus) {
		super(errorStatus);
	}
}
