package com.eventorback.llm;

/**
 * LLM 호출 창구. 지금은 Gemini 구현 하나({@link GeminiClient}).
 * 직접 부르지 말고 {@link LlmService} 를 거친다(한도·사용량 관리).
 *
 * <p>규칙: 공개된 글(이벤트 페이지 본문 등)만 보낸다. 회원 정보·개인정보는 절대 넣지 않는다.</p>
 */
public interface LlmClient {

	boolean enabled();

	LlmResult generate(LlmRequest request);
}
