package com.eventorback.global.util;

import java.security.SecureRandom;

public class NumberUtil {
	private static final SecureRandom random = new SecureRandom(); // 인증번호는 예측할 수 없어야 한다

	public static String createRandom(int length) {
		StringBuilder randomNumber = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			randomNumber.append(random.nextInt(10)); // 0부터 9까지 난수
		}
		return randomNumber.toString();
	}
}
