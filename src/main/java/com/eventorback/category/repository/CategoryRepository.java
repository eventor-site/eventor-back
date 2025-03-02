package com.eventorback.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.eventorback.category.domain.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {

	boolean existsByName(String name);

	Optional<Category> findByName(String name);

	@Query(value = """
		    WITH RECURSIVE categories_hierarchy AS (
		        SELECT category_id, parent_category_id
		        FROM categories
		        WHERE name = :categoryName  -- 카테고리 이름을 기준으로 조회
		        UNION ALL
		        SELECT c.category_id, c.parent_category_id
		        FROM categories c
		        INNER JOIN categories_hierarchy ch ON c.parent_category_id = ch.category_id
		    )
		    SELECT category_id FROM categories_hierarchy
		""", nativeQuery = true)
	List<Long> getCategoryIdsByName(String categoryName);

	@Query(value = """
		    WITH RECURSIVE categories_hierarchy AS (
		        SELECT category_id, parent_category_id, name
		        FROM categories
		        WHERE name = :categoryName  -- 카테고리 이름을 기준으로 조회
		        UNION ALL
		        SELECT c.category_id, c.parent_category_id, c.name
		        FROM categories c
		        INNER JOIN categories_hierarchy ch ON c.parent_category_id = ch.category_id
		    )
		    SELECT name FROM categories_hierarchy
		""", nativeQuery = true)
	List<String> getCategoryNames(String categoryName);

}