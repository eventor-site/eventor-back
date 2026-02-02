package com.eventorback.auth.dto.request;

public record LoginRequest(
	String identifier,
	String password) {
}
