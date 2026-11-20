package com.eventorback.global.jpa;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * HQL(QueryDSL)에서 MySQL FULLTEXT 검색을 쓰기 위한 함수 등록.
 * {@code function('match_against', post.title, '"키워드"')} → {@code MATCH(title) AGAINST('"키워드"' IN BOOLEAN MODE)}
 * <p>
 * META-INF/services/org.hibernate.boot.model.FunctionContributor 에 등록돼 있다.
 */
public class MySqlFunctionContributor implements FunctionContributor {

	@Override
	public void contributeFunctions(FunctionContributions functionContributions) {
		functionContributions.getFunctionRegistry().registerPattern(
			"match_against",
			"match(?1) against(?2 in boolean mode)",
			functionContributions.getTypeConfiguration().getBasicTypeRegistry().resolve(StandardBasicTypes.DOUBLE));
	}
}
