package com.eventorback.search.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import com.eventorback.global.exception.NotFoundException;
import com.eventorback.global.exception.payload.ErrorStatus;

public class ElasticSearchNotFoundException extends NotFoundException {
	public ElasticSearchNotFoundException() {
		super(
			ErrorStatus.from("ElasticSearch 에서 찾을 수 없음",
				HttpStatus.NOT_FOUND,
				LocalDateTime.now()));
	}
}
