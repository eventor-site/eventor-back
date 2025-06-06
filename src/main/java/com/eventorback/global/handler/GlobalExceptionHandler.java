package com.eventorback.global.handler;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.eventorback.global.dto.ApiResponse;
import com.eventorback.global.exception.GlobalException;
import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ApiResponse<Void>> handleGlobalException(GlobalException e) {
		ErrorStatus errorStatus = e.getErrorStatus();
		return ApiResponse.createError(errorStatus.getStatus(), errorStatus.getMessage());
	}

	// @ExceptionHandler(GlobalException.class)
	// public ResponseEntity<String> handleGlobalException(GlobalException e) {
	// 	return ResponseEntity.status(e.getErrorStatus().getStatus()).body(e.getMessage());
	// }

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

	// 500 INTERNAL SERVER ERROR (서버 내부 오류)
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<String>> handleException(Exception e) {
		String errMsg = getFullStackTrace(e);
		log.error(errMsg);
		return ApiResponse.createError(HttpStatus.INTERNAL_SERVER_ERROR, errMsg);
	}

	private String getFullStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}