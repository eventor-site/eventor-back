package com.eventorback.category.domain.dto.response;

import lombok.Builder;

@Builder
public record GetCategoryNameResponse(
	Long categoryId,
	String name) {
}
