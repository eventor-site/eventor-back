package com.eventorback.category.domain.dto.request;

public record CreateCategoryRequest(
	String name,
	String parentCategoryName) {
}
