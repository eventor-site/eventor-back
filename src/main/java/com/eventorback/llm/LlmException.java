package com.eventorback.llm;

/**
 * LLM 호출 실패.
 * rateLimited(429): 한도 초과 → 쉬었다가 다시. retryable(429·5xx·연결 오류): 일시적 → 다음 주기에 다시(작업을 실패로 기록하지 않는다).
 */
public class LlmException extends RuntimeException {
	private final boolean rateLimited;
	private final boolean retryable;

	public LlmException(String message, boolean rateLimited) {
		this(message, rateLimited, rateLimited);
	}

	public LlmException(String message, boolean rateLimited, boolean retryable) {
		super(message);
		this.rateLimited = rateLimited;
		this.retryable = retryable;
	}

	public LlmException(String message, Throwable cause) {
		super(message, cause);
		this.rateLimited = false;
		this.retryable = true;
	}

	public boolean isRateLimited() {
		return rateLimited;
	}

	public boolean isRetryable() {
		return retryable;
	}
}
