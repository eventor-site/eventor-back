package com.eventorback.global.util;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordUtil {
	private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
	private static final String DIGITS = "0123456789";
	private static final String SPECIAL_CHARS = "!@#$%^&*()_+[]{}|;:,.<>?";
	private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
	private static final Random RANDOM = new SecureRandom();

	public static String generateSecurePassword(int length) {
		StringBuilder password = new StringBuilder();

		// 필수 조건을 만족하도록 각각 1개씩 추가
		password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
		password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
		password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
		password.append(SPECIAL_CHARS.charAt(RANDOM.nextInt(SPECIAL_CHARS.length())));

		// 나머지 문자 채우기
		for (int i = 4; i < length; i++) {
			password.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
		}

		// 랜덤하게 섞기
		return shuffleString(password.toString());
	}

	private static String shuffleString(String input) {
		char[] chars = input.toCharArray();
		for (int i = chars.length - 1; i > 0; i--) {
			int j = RANDOM.nextInt(i + 1);
			char temp = chars[i];
			chars[i] = chars[j];
			chars[j] = temp;
		}
		return new String(chars);
	}

	public static boolean isValidPassword(String password) {
		String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+]).{8,}$";
		return password.matches(passwordRegex);
	}
}