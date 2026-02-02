package com.eventorback.global.exception;

import org.springframework.security.core.AuthenticationException;

public class UserWithdrawAuthenticationException extends AuthenticationException {
	public UserWithdrawAuthenticationException(String msg) {
		super(msg);
	}
}
