package com.eventorback.global.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.Getter;

@Getter
public class ServerException extends GlobalException {
	public ServerException(String message) {
		super(ErrorStatus.from(
			message,
			HttpStatus.INTERNAL_SERVER_ERROR,
			LocalDateTime.now())
		);
	}
}
