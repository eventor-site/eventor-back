package com.eventorback.llm;

import java.util.Map;

import tools.jackson.databind.JsonNode;

/**
 * @param json         스키마에 맞춘 응답
 * @param inputTokens  입력 토큰
 * @param outputTokens 출력 토큰
 * @param toolTokens   URL 읽기 등 도구가 쓴 토큰
 * @param urlStatus    URL 읽기 결과(주소 → 성공 여부)
 */
public record LlmResult(JsonNode json, long inputTokens, long outputTokens, long toolTokens, Map<String, Boolean> urlStatus) {
}
