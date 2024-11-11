package com.eventorback.category.repository.impl;

import static com.sikyeojoback.category.domain.entity.QCategory.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sikyeojoback.category.domain.dto.response.GetCategoryNameResponse;
import com.sikyeojoback.category.repository.CustomCategoryRepository;

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
}
