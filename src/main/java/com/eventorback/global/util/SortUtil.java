package com.eventorback.global.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

/**
 * @author 이경헌
 * Spring Data 의 {@link Sort} 객체를 QueryDSL 의 {@link OrderSpecifier} 배열로 변환하는 유틸리티 클래스입니다.
 */
public class SortUtil {

	/**
	 * <p>{@link Pageable} 객체의 정렬 정보를 {@link OrderSpecifier} 배열로 변환합니다.</p>
	 *
	 * @param pageable {@link Pageable} 객체로, 정렬 및 페이지 정보를 포함합니다.
	 * @param qClass   QueryDSL 에서 사용할 {@link EntityPathBase} 객체입니다. 이 객체는 쿼리의 엔티티 경로를 정의합니다.
	 * @param <T>      엔티티의 타입을 나타냅니다.
	 * @return {@link OrderSpecifier} 배열을 반환합니다. {@link Pageable} 객체의 정렬 정보에 따라 생성된 정렬 조건을 포함합니다.
	 */
	public static <T> OrderSpecifier<?>[] getSort(Pageable pageable, EntityPathBase<T> qClass) {
		return pageable.getSort().stream().map(order ->
				new OrderSpecifier(
					Order.valueOf(order.getDirection().name()),
					Expressions.path(Object.class, qClass, order.getProperty())
				)).toList()
			.toArray(new OrderSpecifier[0]);
	}

	public static OrderSpecifier<?>[] getSort(Pageable pageable, List<EntityPathBase<?>> entityPaths) {
		return pageable.getSort().stream().map(order -> {
			String property = order.getProperty();
			Order direction = Order.valueOf(order.getDirection().name());

			// 속성을 가진 엔티티 찾기
			EntityPathBase<?> targetEntity = entityPaths.stream()
				.filter(entity -> hasProperty(entity, property))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("정렬할 수 없는 필드: " + property));

			// QueryDSL PathBuilder 를 활용하여 동적으로 정렬 컬럼 생성
			PathBuilder<?> entityPath = new PathBuilder<>(targetEntity.getType(), targetEntity.getMetadata());
			ComparableExpressionBase<?> expression = entityPath.getComparable(property, Comparable.class);

			return new OrderSpecifier<>(direction, expression);
		}).toArray(OrderSpecifier[]::new);
	}

	/**
	 * 엔티티가 해당 필드를 포함하는지 확인하는 메서드
	 */
	private static boolean hasProperty(EntityPathBase<?> entity, String property) {
		return Arrays.stream(entity.getType().getDeclaredFields())
			.map(Field::getName)
			.anyMatch(fieldName -> fieldName.equals(property));
	}

}