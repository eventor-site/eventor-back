package com.eventorback.llm;

import java.util.Map;

/**
 * @param model       모델 이름
 * @param instruction 역할·규칙(시스템 지시)
 * @param input       처리할 글(공개된 글만)
 * @param schema      응답 JSON 스키마(OpenAPI 형식)
 * @param urlContext  입력에 적은 URL 을 모델이 직접 읽게 할지(토큰을 많이 쓴다: URL 하나에 약 5천 토큰)
 */
public record LlmRequest(String model, String instruction, String input, Map<String, Object> schema, boolean urlContext) {
}
