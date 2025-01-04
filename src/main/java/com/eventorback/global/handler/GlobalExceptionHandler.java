package com.eventorback.global.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eventorback.postrecommend.exception.PostRecommendAlreadyExistsException;

@ControllerAdvice
public class GlobalExceptionHandler {

	// @ExceptionHandler(NotFoundException.class)
	// public ResponseEntity<Void> handleUnauthorizedException() {
	// 	return ResponseEntity.status(404).build();
	// }

	@ExceptionHandler(PostRecommendAlreadyExistsException.class)
	public ResponseEntity<String> handleRecommendAlreadyExistsException(PostRecommendAlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}
}