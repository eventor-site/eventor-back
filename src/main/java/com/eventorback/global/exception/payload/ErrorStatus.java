package com.eventorback.global.exception.payload;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorStatus {
	private String message;
	private HttpStatus status;
	private LocalDateTime timestamp;

	public static ErrorStatus from(String errorMessage, HttpStatus status, LocalDateTime timestamp) {
		return ErrorStatus.builder()
			.message(errorMessage)
			.status(status)
			.timestamp(timestamp)
			.build();
	}
}
