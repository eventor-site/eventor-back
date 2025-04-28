package com.eventorback.user.domain.dto.request;

import java.time.LocalDateTime;

public record UpdateLoginAtRequest(
	Long userId,
	LocalDateTime loginAt) {
}
