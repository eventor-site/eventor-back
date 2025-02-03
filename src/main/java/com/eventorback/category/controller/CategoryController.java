package com.eventorback.category.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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

import com.eventorback.category.domain.dto.request.CreateCategoryRequest;
import com.eventorback.category.domain.dto.request.UpdateCategoryRequest;
import com.eventorback.category.domain.dto.response.GetCategoryListResponse;
import com.eventorback.category.domain.dto.response.GetCategoryNameResponse;
import com.eventorback.category.domain.dto.response.GetCategoryResponse;
import com.eventorback.category.service.impl.CategoryServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/categories")
public class CategoryController {
	private final CategoryServiceImpl categoryService;

	@GetMapping("/search")
	public ResponseEntity<List<GetCategoryNameResponse>> getCategories(@RequestParam String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(categoryService.searchCategories(keyword));
	}

	@GetMapping
	public ResponseEntity<List<GetCategoryListResponse>> getCategories() {
		return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategories());
	}

	@GetMapping("/paging")
	public ResponseEntity<Page<GetCategoryListResponse>> getCategories(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategories(pageable));
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<GetCategoryResponse> getCategory(@PathVariable Long categoryId) {
		return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategory(categoryId));
	}

	@PostMapping
	public ResponseEntity<Void> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
		categoryService.createCategory(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{categoryId}")
	public ResponseEntity<Void> updateCategory(@PathVariable Long categoryId,
		@Valid @RequestBody UpdateCategoryRequest request) {
		categoryService.updateCategory(categoryId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
		categoryService.deleteCategory(categoryId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
