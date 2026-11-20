package com.eventorback.search.repository;

import static com.eventorback.ai.domain.entity.QPostInsight.*;
import static com.eventorback.category.domain.entity.QCategory.*;
import static com.eventorback.comment.domain.entity.QComment.*;
import static com.eventorback.event.domain.entity.QEvent.*;
import static com.eventorback.hotdeal.domain.entity.QHotDeal.*;
import static com.eventorback.image.domain.entity.QImage.*;
import static com.eventorback.post.domain.entity.QPost.*;
import static com.eventorback.status.domain.entity.QStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.eventorback.search.document.dto.reponse.SearchPostsResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

/**
 * 게시물 검색(Elasticsearch 대체). 제목은 MySQL FULLTEXT(ngram) 구문 검색, 그 외는 일반 조건.
 */
@Repository
@RequiredArgsConstructor
public class PostSearchRepository {
	/** ngram 토큰 크기(MySQL 기본 2). 이보다 짧은 검색어는 FULLTEXT로 찾을 수 없어 LIKE로 찾는다 */
	private static final int NGRAM_TOKEN_SIZE = 2;

	private final JPAQueryFactory queryFactory;

	public Page<SearchPostsResponse> search(Pageable pageable, String keyword, List<Long> categoryIds,
		String eventStatusName, String endType, String domain, String benefit) {
		BooleanBuilder where = new BooleanBuilder(status.name.eq("작성됨"));

		if (!categoryIds.isEmpty()) {
			where.and(post.category.categoryId.in(categoryIds));
		}
		if (keyword != null && !keyword.isBlank()) {
			where.and(titleMatches(keyword.trim()).or(event.endType.eq(keyword.trim())));
		}
		where.and(eventStatusCondition(eventStatusName, LocalDateTime.now()));
		if (endType != null && !endType.isBlank()) {
			where.and(event.endType.eq(endType));
		}
		// AI 분석(post_insights) 기준 분야·혜택 유형
		if (domain != null && !domain.isBlank()) {
			where.and(JPAExpressions.selectOne().from(postInsight)
				.where(postInsight.postId.eq(post.postId), postInsight.domain.eq(domain)).exists());
		}
		if (benefit != null && !benefit.isBlank()) {
			where.and(JPAExpressions.selectOne().from(postInsight)
				.where(postInsight.postId.eq(post.postId), postInsight.benefitTypes.contains(benefit)).exists());
		}

		// 1단계: 정렬·페이지로 글 id 만 고른다(서브쿼리 없이 → 핫딜 6.8만 행도 빠름)
		List<Long> pageIds = queryFactory
			.select(post.postId)
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(post.event, event)
			.leftJoin(post.hotDeal, hotDeal)
			.where(where)
			.orderBy(orderBy(pageable))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		// 2단계: 고른 글만 썸네일·댓글 수·AI 분석 같은 서브쿼리를 계산한다
		List<SearchPostsResponse> fetched = pageIds.isEmpty() ? List.of() : queryFactory
			.select(Projections.constructor(SearchPostsResponse.class,
				post.postId,
				category.name,
				status.name,
				post.writer,
				post.writerGrade,
				post.title,
				Expressions.nullExpression(String.class),
				post.recommendationCount,
				post.viewCount,
				post.createdAt,
				hotDeal.productName,
				hotDeal.shoppingMall,
				Expressions.nullExpression(String.class),
				Expressions.nullExpression(Integer.class),
				event.startTime,
				event.endTime,
				event.endType,
				JPAExpressions.select(image.url.min()).from(image)
					.where(image.post.postId.eq(post.postId), image.isThumbnail.isTrue()),
				JPAExpressions.select(image.type.min()).from(image)
					.where(image.post.postId.eq(post.postId), image.isThumbnail.isTrue()),
				JPAExpressions.select(comment.count()).from(comment)
					.where(comment.post.postId.eq(post.postId), comment.status.name.ne("삭제됨")),
				post.isFixed,
				hotDeal.price,
				// 본문 미리보기는 정렬 대상(6.8만 행)에 넣으면 느려져서, 페이지 행만 따로 읽는다(아래 excerpts)
				Expressions.nullExpression(String.class),
				JPAExpressions.select(postInsight.valueNote).from(postInsight).where(postInsight.postId.eq(post.postId)),
				JPAExpressions.select(postInsight.domain).from(postInsight).where(postInsight.postId.eq(post.postId))))
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(post.event, event)
			.leftJoin(post.hotDeal, hotDeal)
			.where(post.postId.in(pageIds))
			.fetch();
		Map<Long, SearchPostsResponse> byId = new HashMap<>();
		fetched.forEach(r -> byId.put(r.postId(), r));
		List<SearchPostsResponse> rows = pageIds.stream().map(byId::get).filter(java.util.Objects::nonNull).toList();

		long total = Optional.ofNullable(queryFactory
			.select(post.count())
			.from(post)
			.join(post.category, category)
			.join(post.status, status)
			.leftJoin(post.event, event)
			.where(where)
			.fetchOne()).orElse(0L);

		Map<Long, String> excerpts = excerpts(rows.stream().map(SearchPostsResponse::postId).toList());
		return new PageImpl<>(rows.stream().map(r -> SearchPostsResponse.addInfo(r, excerpts.get(r.postId()))).toList(),
			pageable, total);
	}

	/** 페이지에 나온 글(최대 100개)만 본문 앞부분을 읽는다 */
	private Map<Long, String> excerpts(List<Long> postIds) {
		if (postIds.isEmpty()) {
			return Map.of();
		}
		Map<Long, String> map = new HashMap<>();
		queryFactory.select(post.postId, Expressions.stringTemplate("substring(cast({0} as String), 1, 600)", post.content))
			.from(post).where(post.postId.in(postIds)).fetch()
			.forEach(t -> map.put(t.get(post.postId), t.get(1, String.class)));
		return map;
	}

	/** 정렬은 정해진 필드만 허용한다. 없거나 모르는 필드면 최신순 */
	private static OrderSpecifier<?>[] orderBy(Pageable pageable) {
		List<OrderSpecifier<?>> orders = new ArrayList<>();
		for (Sort.Order order : pageable.getSort()) {
			Order direction = order.isAscending() ? Order.ASC : Order.DESC;
			ComparableExpressionBase<?> path = switch (order.getProperty()) {
				case "createdAt" -> post.createdAt;
				case "viewCount" -> post.viewCount;
				case "recommendationCount" -> post.recommendationCount;
				case "startTime" -> event.startTime;
				case "endTime" -> event.endTime;
				default -> null;
			};
			if (path != null) {
				orders.add(new OrderSpecifier<>(direction, path, OrderSpecifier.NullHandling.NullsLast));
			}
		}
		if (orders.isEmpty()) {
			orders.add(post.createdAt.desc());
		}
		orders.add(post.postId.desc());
		return orders.toArray(OrderSpecifier[]::new);
	}

	private static BooleanExpression titleMatches(String keyword) {
		String cleaned = keyword.replaceAll("[+\\-<>()~*\"@]", " ").replaceAll("\\s+", " ").trim();
		if (cleaned.replace(" ", "").length() < NGRAM_TOKEN_SIZE) {
			return post.title.contains(keyword);
		}
		return Expressions.numberTemplate(Double.class, "function('match_against', {0}, {1})",
			post.title, Expressions.constant("\"" + cleaned + "\"")).gt(0.0);
	}

	private static BooleanExpression eventStatusCondition(String eventStatusName, LocalDateTime now) {
		if (eventStatusName == null) {
			return null;
		}
		return switch (eventStatusName) {
			case "예정" -> event.startTime.gt(now);
			// 종료일이 없는 이벤트는 Gemini 가 링크를 읽고 "종료"로 판정한 것을 뺀다
			case "진행중" -> event.eventPostId.isNotNull().and(
				event.endTime.isNull().or(event.startTime.loe(now).and(event.endTime.gt(now))))
				.and(JPAExpressions.selectOne().from(postInsight)
					.where(postInsight.postId.eq(post.postId), postInsight.liveStatus.eq("종료")).notExists());
			case "마감" -> event.endTime.lt(now);
			default -> null;
		};
	}
}
