package com.eventorback.search.service;

import java.time.LocalDateTime;

/** 테스트에서 패키지 전용 메서드를 부르기 위한 통로 */
public final class KeywordServiceAccess {
	private KeywordServiceAccess() {
	}

	public static LocalDateTime bucket(LocalDateTime time, int offset) {
		return KeywordService.bucket(time, offset);
	}
}
