package com.eventorback.category.domain.dto.response;

import com.eventorback.category.domain.entity.Category;

import lombok.Builder;

@Builder
public record GetCategoryResponse(
	Long categoryId,
	String name,
	String parentCategoryName) {
	public static GetCategoryResponse fromEntity(Category category) {
		String parentCategoryName =
			category.getParentCategory() != null ? category.getParentCategory().getName() : null;

		return GetCategoryResponse.builder()
			.categoryId(category.getCategoryId())
			.name(category.getName())
			.parentCategoryName(parentCategoryName)
			.build();
	}
}
