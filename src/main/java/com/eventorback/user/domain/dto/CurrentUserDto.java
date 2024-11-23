package com.eventorback.user.domain.dto;

import java.util.List;

import lombok.Builder;

@Builder
public record CurrentUserDto(
	Long userId,
	List<String> roles
) {
}

