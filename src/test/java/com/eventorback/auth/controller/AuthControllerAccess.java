package com.eventorback.auth.controller;

/** 테스트에서 패키지 전용 메서드를 부르기 위한 통로 */
public final class AuthControllerAccess {
	private AuthControllerAccess() {
	}

	public static String safeRedirect(String redirect) {
		return AuthController.safeRedirect(redirect);
	}
}
