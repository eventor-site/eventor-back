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
import com.eventorback.category.domain.entity.CategoryClosure;
import com.eventorback.category.exception.CategoryAlreadyExistsException;
import com.eventorback.category.exception.CategoryNotFoundException;
import com.eventorback.category.repository.CategoryClosureRepository;
import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.category.service.CategoryService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;
	private final CategoryClosureRepository categoryClosureRepository;

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
		Category newCategory = categoryRepository.save(Category.toEntity(request, parentCategory));

		// 클로저 테이블에 관계 추가
		createClosureRelations(newCategory, parentCategory);
	}

	@Override
	public void createClosureRelations(Category newCategory, Category parentCategory) {
		// 1. 부모 카테고리의 모든 조상 관계 가져오기
		List<CategoryClosure> parentAncestors = categoryClosureRepository.findByDescendant(parentCategory);

		// 2. 부모 카테고리의 조상 → 새 자식 관계 생성
		for (CategoryClosure parentAncestor : parentAncestors) {
			CategoryClosure closure = CategoryClosure.builder()
				.ancestor(parentAncestor.getAncestor()) // 부모의 조상
				.descendant(newCategory) // 새 자식
				.depth(parentAncestor.getDepth() + 1) // 깊이 증가
				.build();
			categoryClosureRepository.save(closure);
		}

		// 3. 부모 자신 → 새 자식 관계 생성
		CategoryClosure parentToChild = CategoryClosure.builder()
			.ancestor(parentCategory) // 부모 자신
			.descendant(newCategory) // 새 자식
			.depth(1L) // 부모에서 바로 연결된 깊이
			.build();
		categoryClosureRepository.save(parentToChild);

		// 4. 새 자식 → 자기 자신 관계 생성
		CategoryClosure selfRelation = CategoryClosure.builder()
			.ancestor(newCategory)
			.descendant(newCategory)
			.depth(0L) // 자기 자신
			.build();
		categoryClosureRepository.save(selfRelation);
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
