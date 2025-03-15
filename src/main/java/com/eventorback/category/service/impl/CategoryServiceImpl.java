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
	public List<String> getCategories(String categoryName) {
		return categoryRepository.getCategoryNames(categoryName);
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
			.orElseThrow(CategoryNotFoundException::new);
		return GetCategoryResponse.fromEntity(category);
	}

	@Override
	public void createCategory(CreateCategoryRequest request) {
		if (categoryRepository.existsByName(request.name())) {
			throw new CategoryAlreadyExistsException();
		}

		Category parentCategory = null;
		if (request.parentCategoryName().isEmpty()) {
			categoryRepository.save(new Category(parentCategory, request.name(), categoryRepository.getMaxGroup() + 1));
		} else {
			parentCategory = categoryRepository.findByName(request.parentCategoryName())
				.orElseThrow(CategoryNotFoundException::new);

			increaseCategory(parentCategory);

			if (parentCategory.getParentCategory() == null) {
				categoryRepository.save(new Category(parentCategory, request.name(), parentCategory.getGroup(),
					parentCategory.getDepth() + 1, categoryRepository.getTotalChildCount(parentCategory.getGroup()),
					0L));
			} else {
				categoryRepository.save(new Category(parentCategory, request.name(), parentCategory.getGroup(),
					parentCategory.getDepth() + 1, parentCategory.getGroupOrder() + parentCategory.getChildCount(),
					0L));
			}

		}

	}

	@Override
	public void updateCategory(Long categoryId, UpdateCategoryRequest request) {
		if (categoryRepository.existsByCategoryIdNotAndName(categoryId, request.name())) {
			throw new CategoryAlreadyExistsException();
		}
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		Category updateParentCategory = null;
		if (request.parentCategoryName() != null) {
			updateParentCategory = categoryRepository.findByName(request.parentCategoryName())
				.orElse(null);
		}

		if (category.getParentCategory() == null && updateParentCategory == null) {
			category.update(request.name());
		} else if (category.getParentCategory() == null) {
			increaseCategory(updateParentCategory);
			updateCategoryInfo(category, updateParentCategory, request);
		} else if (updateParentCategory == null) {
			decreaseCategory(category);
			updateCategoryInfo(category, updateParentCategory, request);
		} else if (category.getParentCategory().getName().equals(request.parentCategoryName())) {
			category.update(request.name());
		} else {
			decreaseCategory(category);
			increaseCategory(updateParentCategory);
			updateCategoryInfo(category, updateParentCategory, request);
		}

	}

	@Override
	public void deleteCategory(Long categoryId) {
		Category category = categoryRepository.findById(categoryId)
			.orElseThrow(CategoryNotFoundException::new);

		decreaseCategory(category);

		categoryRepository.deleteById(categoryId);
	}

	@Override
	public void increaseCategory(Category parentCategory) {
		parentCategory.addChildCount();

		if (parentCategory.getParentCategory() != null) {
			List<Category> updateList = categoryRepository.getGreaterGroupOrder(parentCategory.getGroup(),
				parentCategory.getGroupOrder() + parentCategory.getChildCount());

			for (Category category : updateList) {
				category.addGroupOrder();
			}
		}
	}

	@Override
	public void decreaseCategory(Category category) {
		if (category.getParentCategory() != null) {
			category.getParentCategory().minusChildCount();
		}

		List<Category> updateList = categoryRepository.getGreaterGroupOrder(category.getGroup(),
			category.getGroupOrder());

		for (Category updateCategory : updateList) {
			updateCategory.minusGroupOrder();
		}
	}

	@Override
	public void updateCategoryInfo(Category category, Category updateParentCategory, UpdateCategoryRequest request) {
		if (updateParentCategory == null) {
			category.update(null, request.name(), categoryRepository.getMaxGroup() + 1);
		} else {
			if (updateParentCategory.getParentCategory() == null) {
				category.update(updateParentCategory, request.name(), updateParentCategory.getGroup(),
					updateParentCategory.getDepth() + 1,
					categoryRepository.getTotalChildCount(updateParentCategory.getGroup()),
					0L);
			} else {
				category.update(updateParentCategory, request.name(), updateParentCategory.getGroup(),
					updateParentCategory.getDepth() + 1,
					updateParentCategory.getGroupOrder() + updateParentCategory.getChildCount(),
					0L);
			}
		}
	}

}
