package com.eventorback.global.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.eventorback.global.dto.ApiResponse;
import com.eventorback.global.exception.GlobalException;
import com.eventorback.global.exception.payload.ErrorStatus;

import lombok.extern.slf4j.Slf4j;

/**
 * API 오류 응답. 응답 본문에는 사용자에게 보여줄 메시지만 담고, 스택 트레이스는 로그에만 남긴다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	public ResponseEntity<ApiResponse<Void>> handleGlobalException(GlobalException e) {
		ErrorStatus errorStatus = e.getErrorStatus();
		return ApiResponse.createError(errorStatus.getStatus(), errorStatus.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException e) {
		return ApiResponse.createFail(e.getBindingResult());
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, MissingServletRequestParameterException.class,
		MethodArgumentTypeMismatchException.class, IllegalArgumentException.class,
		org.springframework.web.multipart.MultipartException.class,
		org.springframework.web.HttpMediaTypeNotSupportedException.class,
		org.springframework.web.bind.MissingRequestValueException.class})
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception e) {
		log.debug("잘못된 요청: {}", e.getMessage());
		return ApiResponse.createError(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
	}

	/** Gemini 한도 초과·일시 오류: 500 이 아니라 503 과 안내 문구 */
	@ExceptionHandler(com.eventorback.llm.LlmException.class)
	public ResponseEntity<ApiResponse<Void>> handleLlm(com.eventorback.llm.LlmException e) {
		return ApiResponse.createError(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE,
			e.isRetryable() ? "AI 사용량이 많아 잠시 뒤에 다시 시도해 주세요." : "AI 처리에 실패했어요.");
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
		return ApiResponse.createError(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 요청 방식입니다.");
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(NoResourceFoundException e) {
		return ApiResponse.createError(HttpStatus.NOT_FOUND, "요청한 주소를 찾을 수 없습니다.");
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponse<Void>> handleTooLarge(MaxUploadSizeExceededException e) {
		return ApiResponse.createError(HttpStatus.PAYLOAD_TOO_LARGE, "파일 용량은 20MB를 넘을 수 없습니다.");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		log.error("처리하지 못한 오류", e);
		return ApiResponse.createError(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
	}
}
