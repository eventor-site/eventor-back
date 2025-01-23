package com.eventorback.category.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.category.domain.dto.request.CreateCategoryRequest;
import com.eventorback.category.domain.dto.request.UpdateCategoryRequest;
import com.eventorback.category.domain.dto.response.GetCategoryNameResponse;
import com.eventorback.category.domain.dto.response.GetCategoryResponse;
import com.eventorback.category.domain.entity.Category;

public interface CategoryService {

	List<GetCategoryNameResponse> searchCategories(String keyword);

	List<GetCategoryResponse> getCategories();

	Page<GetCategoryResponse> getCategories(Pageable pageable);

	GetCategoryResponse getCategory(Long categoryId);

	void createCategory(CreateCategoryRequest request);

	void createClosureRelations(Category newCategory, Category parentCategory);

	void updateCategory(Long categoryId, UpdateCategoryRequest request);

	void deleteCategory(Long categoryId);

}
