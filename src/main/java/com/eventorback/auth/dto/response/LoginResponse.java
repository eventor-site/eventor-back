package com.eventorback.auth.dto.response;

/**
 * 로그인 결과. 토큰은 HttpOnly 쿠키로 내려가고 본문에는 상태만 담는다.
 *
 * @param result {@code LOGGED_IN}: 로그인 완료 / {@code DORMANT}: 휴면 계정(해제 확인 필요, auth_ticket 쿠키 발급됨)
 */
public record LoginResponse(String result) {
	public static final LoginResponse LOGGED_IN = new LoginResponse("LOGGED_IN");
	public static final LoginResponse DORMANT = new LoginResponse("DORMANT");
}
