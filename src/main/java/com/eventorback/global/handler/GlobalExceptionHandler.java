package com.eventorback.global.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eventorback.global.exception.GlobalException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<String> handleGlobalException(GlobalException e) {
		return ResponseEntity.status(e.getErrorStatus().getStatus()).body(e.getMessage());
	}

	// // 400 BAD REQUEST (잘못된 요청)
	// @ExceptionHandler(IllegalArgumentException.class)
	// public ResponseEntity<String> handleBadRequest(IllegalArgumentException e) {
	// 	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	// }
	//
	// // 404 NOT FOUND (찾을 수 없음)
	// @ExceptionHandler(NoSuchElementException.class)
	// public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
	// 	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 데이터를 찾을 수 없습니다.");
	// }
	//
	// // 500 INTERNAL SERVER ERROR (서버 내부 오류)
	// @ExceptionHandler(Exception.class)
	// public ResponseEntity<String> handleException() {
	// 	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러");
	// }
}