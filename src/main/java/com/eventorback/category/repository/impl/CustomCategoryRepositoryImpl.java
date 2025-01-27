package com.eventorback.category.repository.impl;

import static com.eventorback.category.domain.entity.QCategory.*;

import java.util.List;

import com.eventorback.category.domain.dto.response.GetCategoryListResponse;
import com.eventorback.category.domain.dto.response.GetCategoryNameResponse;
import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.repository.CustomCategoryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {
	private final JPAQueryFactory queryFactory;

	@Override
	public List<GetCategoryNameResponse> searchCategories(String keyword) {
		return queryFactory
			.select(category.categoryId, category.name)
			.from(category)
			.where(category.name.contains(keyword))
			.fetch()
			.stream()
			.map(st -> new GetCategoryNameResponse(st.get(category.categoryId), st.get(category.name)))
			.toList();
	}

	@Override
	public List<GetCategoryListResponse> getCategories() {
		return queryFactory
			.select(Projections.constructor(
				GetCategoryListResponse.class,
				category.categoryId,
				category.name,
				category.depth))
			.from(category)
			.orderBy(category.group.asc(), category.groupOrder.asc())
			.fetch();
	}

	@Override
	public Long getMaxGroup() {
		Long maxGroup = queryFactory
			.select(category.group.max())
			.from(category)
			.fetchOne();
		return maxGroup != null ? maxGroup : 1L;
	}

	@Override
	public Long getTotalChildCount(Long group) {
		return queryFactory
			.select(category.childCount.sum())
			.from(category)
			.where(category.group.eq(group))
			.fetchOne();
	}

	@Override
	public List<Category> getGreaterGroupOrder(Long group, Long groupOrder) {
		return queryFactory
			.select(category)
			.from(category)
			.where(category.group.eq(group), category.groupOrder.goe(groupOrder))    //greater than
			.fetch();
	}
}
