package com.eventorback.category.domain.entity;

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
public class CategoryClosure {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_closure_id")
	private Long categoryClosureId;

	@ManyToOne
	@JoinColumn(name = "ancestor_id")
	private Category ancestor;

	@ManyToOne
	@JoinColumn(name = "descendant_id")
	private Category descendant;

	@Column(name = "depth")
	private Long depth;

	@Builder
	public CategoryClosure(Category ancestor, Category descendant, Long depth) {
		this.ancestor = ancestor;
		this.descendant = descendant;
		this.depth = depth;
	}
}
