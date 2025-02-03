package com.eventorback.category.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.category.domain.dto.response.GetCategoryListResponse;
import com.eventorback.category.domain.dto.response.GetCategoryNameResponse;
import com.eventorback.category.domain.entity.Category;

public interface CustomCategoryRepository {

	List<GetCategoryNameResponse> searchCategories(String keyword);
	
	Page<GetCategoryListResponse> getCategories(Pageable pageable);

	Long getMaxGroup();

	Long getTotalChildCount(Long group);

	List<Category> getGreaterGroupOrder(Long group, Long groupOrder);
}
