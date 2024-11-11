package com.eventorback.category.repository;

import java.util.List;

import com.sikyeojoback.category.domain.dto.response.GetCategoryNameResponse;

public interface CustomCategoryRepository {

	List<GetCategoryNameResponse> searchCategories(String keyword);
}
