package com.eventorback.tour.exception;

import com.eventorback.global.exception.ServerException;

public class GecoderAPIServerException extends ServerException {
	public GecoderAPIServerException() {
		super("국토교통부에서 데이터를 받아오는 도중 문제가 발생하였습니다.");
	}
}
