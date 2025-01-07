package com.eventorback.user.domain.dto.request;

import java.time.LocalDateTime;

public record UpdateLastLoginTimeRequest(
	Long userId,
	LocalDateTime lastLoginTime) {
}
