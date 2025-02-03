package com.eventorback.category.domain.dto.response;

import com.eventorback.category.domain.entity.Category;

import lombok.Builder;

@Builder
public record GetCategoryListResponse(
	Long categoryId,
	String name,
	Long depth
) {
	public static GetCategoryListResponse fromEntity(Category category) {
		return GetCategoryListResponse.builder()
			.categoryId(category.getCategoryId())
			.name(category.getName())
			.depth(category.getDepth())
			.build();
	}
}
