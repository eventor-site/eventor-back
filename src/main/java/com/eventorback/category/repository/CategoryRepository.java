package com.eventorback.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.category.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {

	boolean existsByName(String name);
}