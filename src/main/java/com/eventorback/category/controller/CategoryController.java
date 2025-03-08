package com.eventorback.category.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.category.domain.dto.request.CreateCategoryRequest;
import com.eventorback.category.domain.dto.request.UpdateCategoryRequest;
import com.eventorback.category.domain.dto.response.GetCategoryListResponse;
import com.eventorback.category.domain.dto.response.GetCategoryNameResponse;
import com.eventorback.category.domain.dto.response.GetCategoryResponse;
import com.eventorback.category.service.impl.CategoryServiceImpl;
import com.eventorback.global.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/categories")
public class CategoryController {
	private final CategoryServiceImpl categoryService;

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<GetCategoryNameResponse>>> searchCategories(@RequestParam String keyword) {
		return ApiResponse.createSuccess(categoryService.searchCategories(keyword));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<String>>> getCategories(@RequestParam String categoryName) {
		return ApiResponse.createSuccess(categoryService.getCategories(categoryName));
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<ApiResponse<Page<GetCategoryListResponse>>> getCategories(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(categoryService.getCategories(pageable));
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<GetCategoryResponse>> getCategory(@PathVariable Long categoryId) {
		return ApiResponse.createSuccess(categoryService.getCategory(categoryId));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
		categoryService.createCategory(request);
		return ApiResponse.createSuccess("카테고리가 추가되었습니다.");
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Void>> updateCategory(@PathVariable Long categoryId,
		@Valid @RequestBody UpdateCategoryRequest request) {
		categoryService.updateCategory(categoryId, request);
		return ApiResponse.createSuccess("카테고리가 수정되었습니다.");
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);
		return ApiResponse.createSuccess("카테고리가 삭제 되었습니다.");
	}

}
