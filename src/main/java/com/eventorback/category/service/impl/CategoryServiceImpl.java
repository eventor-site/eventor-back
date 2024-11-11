package com.eventorback.category.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.category.domain.dto.request.CreateCategoryRequest;
import com.eventorback.category.domain.dto.request.UpdateCategoryRequest;
import com.eventorback.category.domain.dto.response.GetCategoryNameResponse;
import com.eventorback.category.domain.dto.response.GetCategoryResponse;
import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.exception.CategoryAlreadyExistsException;
import com.eventorback.category.exception.CategoryNotFoundException;
import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.category.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	@Override
	public List<GetCategoryNameResponse> searchCategories(String keyword) {
		return categoryRepository.searchCategories(keyword);
	}

	@Override
	public List<GetCategoryResponse> getCategories() {
		return categoryRepository.findAll().stream().map(GetCategoryResponse::fromEntity).toList();
	}

	@Override
	public Page<GetCategoryResponse> getCategories(Pageable pageable) {
		return null;
	}

	@Override
	public GetCategoryResponse getCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		return GetCategoryResponse.fromEntity(category);
	}

	@Override
	public void createCategory(CreateCategoryRequest request) {
		if (categoryRepository.existsByName(request.name())) {
			throw new CategoryAlreadyExistsException(request.name());
		}
		Category parentCategory = null;
		if (request.parentCategoryId() != null) {
			parentCategory = categoryRepository.findById(request.parentCategoryId()).orElse(null);
		}
		categoryRepository.save(Category.toEntity(request, parentCategory));

	}

	@Override
	public void updateCategory(Long categoryId, UpdateCategoryRequest request) {
		if (categoryRepository.existsByName(request.name())) {
			throw new CategoryAlreadyExistsException(request.name());
		}
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		
		Category parentCategory = null;
		if (request.parentCategoryId() != null) {
			parentCategory = categoryRepository.findById(request.parentCategoryId()).orElse(null);
		}
		category.updateCategory(request.name(), parentCategory);
	}

	@Override
	public void deleteCategory(Long categoryId) {
		categoryRepository.deleteById(categoryId);
	}
}
