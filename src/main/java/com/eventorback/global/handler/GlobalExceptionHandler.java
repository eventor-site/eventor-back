package com.eventorback.global.handler;

import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

	// @ExceptionHandler(NotFoundException.class)
	// public ResponseEntity<Void> handleUnauthorizedException() {
	// 	return ResponseEntity.status(404).build();
	// }

	// @ExceptionHandler(AccessDeniedException.class)
	// public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException ex) {
	// 	return ResponseEntity.status(HttpStatus.OK).body(ex.getMessage());
	// }
}