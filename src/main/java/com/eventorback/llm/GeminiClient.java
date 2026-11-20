package com.eventorback.llm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.eventorback.global.config.EventorProperties;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

/**
 * Gemini API generateContent(REST). 키는 헤더(x-goog-api-key)로 보내 로그·URL에 남지 않게 한다.
 * 응답은 responseSchema 로 JSON 구조를 강제한다(URL 읽기 도구와 함께 써도 된다: 2026-11-20 확인).
 */
@Component
public class GeminiClient implements LlmClient {
	private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
	private static final ObjectMapper MAPPER = JsonMapper.builder().build();

	private final EventorProperties.Gemini config;
	private final RestClient restClient;

	public GeminiClient(EventorProperties properties, RestClient.Builder builder) {
		this.config = properties.gemini();
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		if (config != null && config.timeout() != null) {
			factory.setConnectTimeout(config.timeout());
			factory.setReadTimeout(config.timeout());
		}
		this.restClient = builder.clone().requestFactory(factory).build();
	}

	@Override
	public boolean enabled() {
		return config != null && config.enabled();
	}

	@Override
	public LlmResult generate(LlmRequest request) {
		if (!enabled()) {
			throw new LlmException("GEMINI_API_KEY 가 설정되지 않았어요.", false);
		}
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("systemInstruction", Map.of("parts", List.of(Map.of("text", request.instruction()))));
		body.put("contents", List.of(Map.of("role", "user", "parts", List.of(Map.of("text", request.input())))));
		if (request.urlContext()) {
			body.put("tools", List.of(Map.of("url_context", Map.of())));
		}
		body.put("generationConfig", Map.of("responseMimeType", "application/json", "responseSchema", request.schema()));

		JsonNode response;
		try {
			response = restClient.post()
				.uri(BASE_URL + request.model() + ":generateContent")
				.header("x-goog-api-key", config.apiKey())
				.contentType(MediaType.APPLICATION_JSON)
				.body(body)
				.retrieve()
				.onStatus(HttpStatusCode::isError, (req, res) -> {
					int status = res.getStatusCode().value();
					throw new LlmException("Gemini 응답 " + status + (status == 429 ? "(한도 초과)" : status >= 500 ? "(일시 오류)" : ""),
						status == 429, status == 429 || status >= 500);
				})
				.body(JsonNode.class);
		} catch (LlmException e) {
			throw e;
		} catch (RestClientException e) {
			throw new LlmException("Gemini 호출 실패", e);
		}
		if (response == null) {
			throw new LlmException("Gemini 빈 응답", false);
		}
		JsonNode candidate = response.path("candidates").path(0);
		String text = null;
		for (JsonNode part : candidate.path("content").path("parts")) {
			if (!part.path("thought").asBoolean(false) && part.has("text")) {
				text = part.path("text").asString();
			}
		}
		JsonNode usage = response.path("usageMetadata");
		long in = usage.path("promptTokenCount").asLong(0);
		long out = usage.path("candidatesTokenCount").asLong(0) + usage.path("thoughtsTokenCount").asLong(0);
		long tool = usage.path("toolUsePromptTokenCount").asLong(0);
		Map<String, Boolean> urlStatus = new HashMap<>();
		for (JsonNode meta : candidate.path("urlContextMetadata").path("urlMetadata")) {
			urlStatus.put(meta.path("retrievedUrl").asString(""),
				"URL_RETRIEVAL_STATUS_SUCCESS".equals(meta.path("urlRetrievalStatus").asString("")));
		}
		if (text == null || text.isBlank()) {
			throw new LlmException("Gemini 결과 없음: " + candidate.path("finishReason").asString("응답 없음"), false);
		}
		try {
			return new LlmResult(MAPPER.readTree(text), in, out, tool, urlStatus);
		} catch (Exception e) {
			throw new LlmException("Gemini 응답이 JSON 이 아니에요", e);
		}
	}

	/** 테스트·점검용: 목록 순서를 유지한 채 문자열 목록으로 */
	static List<String> strings(JsonNode array) {
		List<String> list = new ArrayList<>();
		array.forEach(n -> list.add(n.asString()));
		return list;
	}
}
