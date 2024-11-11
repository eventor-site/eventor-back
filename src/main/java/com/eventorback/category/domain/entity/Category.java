package com.eventorback.category.domain.entity;

import com.eventorback.category.domain.dto.request.CreateCategoryRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long categoryId;

	@ManyToOne
	@JoinColumn(name = "parent_category_id")
	private Category parentCategory;

	@Column(name = "name")
	private String name;

	@Builder
	public Category(String name, Category parentCategory) {
		this.name = name;
		this.parentCategory = parentCategory;
	}

	public static Category toEntity(CreateCategoryRequest request, Category parentCategory) {
		return Category.builder()
			.name(request.name())
			.parentCategory(parentCategory)
			.build();
	}

	public void updateCategory(String name, Category parentCategory) {
		this.name = name;
		this.parentCategory = parentCategory;
	}
}
