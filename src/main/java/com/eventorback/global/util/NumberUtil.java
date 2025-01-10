package com.eventorback.global.util;

import java.util.Random;

public class NumberUtil {
	private static final Random random = new Random();

	public static String createRandom(int length) {
		StringBuilder randomNumber = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			randomNumber.append(random.nextInt(10)); // 0부터 9까지 난수
		}
		return randomNumber.toString();
	}
}
