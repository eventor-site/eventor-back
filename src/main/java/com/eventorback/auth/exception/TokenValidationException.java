package com.eventorback.auth.exception;

import com.eventorback.global.exception.UnauthorizedException;

public class TokenValidationException extends UnauthorizedException {
	public TokenValidationException() {
		super("로그인 인증이 만료되었습니다.");
	}
}
