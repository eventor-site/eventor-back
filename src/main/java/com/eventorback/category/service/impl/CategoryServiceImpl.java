package com.eventorback.category.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.category.domain.dto.request.CreateCategoryRequest;
import com.eventorback.category.domain.dto.request.UpdateCategoryRequest;
import com.eventorback.category.domain.dto.response.GetCategoryListResponse;
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
	public List<GetCategoryListResponse> getCategories() {
		return categoryRepository.getCategories();
	}

	@Override
	public Page<GetCategoryListResponse> getCategories(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return categoryRepository.getCategories(PageRequest.of(page, pageSize));
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
		if (request.parentCategoryName().isEmpty()) {
			categoryRepository.save(new Category(parentCategory, request.name(), categoryRepository.getMaxGroup() + 1));
		} else {
			parentCategory = categoryRepository.findByName(request.parentCategoryName())
				.orElseThrow(() -> new CategoryNotFoundException(request.parentCategoryName()));

			parentCategory.addChildCount();

			if (parentCategory.getParentCategory() == null) {
				categoryRepository.save(new Category(parentCategory, request.name(), parentCategory.getGroup(),
					parentCategory.getDepth() + 1, categoryRepository.getTotalChildCount(parentCategory.getGroup()),
					0L));

			} else {
				List<Category> updateList = categoryRepository.getGreaterGroupOrder(parentCategory.getGroup(),
					parentCategory.getGroupOrder() + parentCategory.getChildCount());

				for (Category category : updateList) {
					category.addGroupOrder();
				}

				categoryRepository.save(new Category(parentCategory, request.name(), parentCategory.getGroup(),
					parentCategory.getDepth() + 1, parentCategory.getGroupOrder() + parentCategory.getChildCount(),
					0L));
			}

		}

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
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(() -> new CategoryNotFoundException(categoryId));

		category.getParentCategory().minusChildCount();

		List<Category> updateList = categoryRepository.getGreaterGroupOrder(category.getGroup(),
			category.getGroupOrder());

		for (Category updateCategory : updateList) {
			updateCategory.minusGroupOrder();
		}
		categoryRepository.deleteById(categoryId);
	}

}
