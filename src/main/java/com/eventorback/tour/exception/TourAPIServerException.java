package com.eventorback.tour.exception;

import com.eventorback.global.exception.ServerException;

public class TourAPIServerException extends ServerException {
	public TourAPIServerException() {
		super("한국관광공사에서 데이터를 받아오는 도중 문제가 발생하였습니다.");
	}
}
