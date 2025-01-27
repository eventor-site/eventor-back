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

	@Column(name = "group")
	private Long group;

	@Column(name = "depth")
	private Long depth;

	@Column(name = "group_order")
	private Long groupOrder;

	@Column(name = "child_count")
	private Long childCount;

	@Builder
	public Category(Category parentCategory, String name, Long group, Long depth, Long groupOrder, Long childCount) {
		this.parentCategory = parentCategory;
		this.name = name;
		this.group = group;
		this.depth = depth;
		this.groupOrder = groupOrder;
		this.childCount = childCount;
	}

	@Builder
	public Category(Category parentCategory, String name, Long group) {
		this.parentCategory = parentCategory;
		this.name = name;
		this.group = group;
		this.depth = 0L;
		this.groupOrder = 0L;
		this.childCount = 0L;
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

	public void addChildCount() {
		this.childCount++;
	}

	public void minusChildCount() {
		this.childCount--;
	}

	public void addGroupOrder() {
		this.groupOrder++;
	}

	public void minusGroupOrder() {
		this.groupOrder--;
	}

}
