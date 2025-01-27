package com.eventorback.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.eventorback.category.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {

	boolean existsByName(String name);

	Optional<Category> findByName(String name);

	@Query(value = """
		    WITH RECURSIVE category_hierarchy AS (
		        SELECT category_id, parent_category_id
		        FROM category
		        WHERE name = :categoryName  -- 카테고리 이름을 기준으로 조회
		        UNION ALL
		        SELECT c.category_id, c.parent_category_id
		        FROM category c
		        INNER JOIN category_hierarchy ch ON c.parent_category_id = ch.category_id
		    )
		    SELECT category_id FROM category_hierarchy
		""", nativeQuery = true)
	List<Long> getCategoryIdsByName(String categoryName);

}