package com.eventorback.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.domain.entity.CategoryClosure;

public interface CategoryClosureRepository extends JpaRepository<CategoryClosure, Long> {

	List<CategoryClosure> findByDescendant(Category descendant);
}