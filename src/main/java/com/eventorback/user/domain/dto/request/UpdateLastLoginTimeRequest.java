package com.eventorback.user.domain.dto.request;

import java.time.LocalDateTime;

public record UpdateLastLoginTimeRequest(
	LocalDateTime lastLoginTime) {
}
