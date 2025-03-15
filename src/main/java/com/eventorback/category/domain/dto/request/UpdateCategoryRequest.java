package com.eventorback.category.domain.dto.request;

import lombok.Builder;

@Builder
public record UpdateCategoryRequest(
	String name,
	String parentCategoryName) {
}
