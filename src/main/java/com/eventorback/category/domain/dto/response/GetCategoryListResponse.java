package com.eventorback.category.domain.dto.response;

import lombok.Builder;

@Builder
public record GetCategoryListResponse(
	Long categoryId,
	String name,
	Long depth) {
}
