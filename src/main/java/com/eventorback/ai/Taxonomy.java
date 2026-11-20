package com.eventorback.ai;

import java.util.List;

/**
 * 분류 기준(content-ops-plan 5절). Gemini 응답은 반드시 이 값들 중에서만 받는다.
 */
public final class Taxonomy {
	private Taxonomy() {
	}

	public static final List<String> DOMAINS = List.of(
		"음식·카페", "패션·뷰티", "디지털·가전", "생활·리빙", "문화·여가", "금융·카드", "여행", "기타");

	public static final List<String> BENEFITS = List.of(
		"할인·쿠폰", "1+1·증정", "응모·추첨", "체험단", "출석·적립", "신규가입", "무료");

	public static final List<String> DIFFICULTY = List.of("쉬움", "보통", "어려움");

	public static final List<String> LIVE = List.of("진행 중", "종료", "불명");
}
