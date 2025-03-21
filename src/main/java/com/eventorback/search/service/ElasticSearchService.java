package com.eventorback.search.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.search.document.dto.reponse.SearchPostsResponse;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {
	private final ElasticsearchClient elasticsearchClient;
	private final CategoryRepository categoryRepository;

	public Page<SearchPostsResponse> searchPosts(Pageable pageable, String keyword, String categoryName,
		String eventStatusName) {
		try {
			int page = Math.max(pageable.getPageNumber() - 1, 0);
			int pageSize = pageable.getPageSize();
			Sort sort = pageable.getSort();

			Pageable newPageable = PageRequest.of(page, pageSize, sort);

			// 카테고리 필터링을 위한 목록 가져오기
			List<String> categoryNames = categoryRepository.getCategoryNames(categoryName);

			SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder()
				.index("post")
				.query(query -> query
					.bool(boolQuery -> {
						// ❌ 삭제된 상태 제외 (Filter Context)
						boolQuery.filter(f -> f
							.bool(b -> b
								.mustNot(mn -> mn
									.term(t -> t
										.field("statusName")
										.value("삭제됨")
									)
								)
							)
						);

						// ✅ 카테고리 필터 적용 (Filter Context)
						if (!categoryNames.isEmpty()) {
							List<FieldValue> categoryFieldValues = categoryNames.stream()
								.map(FieldValue::of)
								.toList();
							boolQuery.filter(f -> f
								.terms(t -> t
									.field("categoryName")
									.terms(v -> v.value(categoryFieldValues))
								)
							);
						}

						// ✅ 키워드 검색 (Query Context, Relevance Score 적용)
						if (keyword != null && !keyword.trim().isEmpty()) {
							boolQuery.must(mustQuery -> mustQuery
								.bool(bq -> bq
									.should(s -> s.match(m -> m
										.field("title")
										.query(keyword)
										.operator(Operator.Or)
									))
									.should(s -> s.match(m -> m
										.field("content")
										.query(keyword)
										.operator(Operator.Or)
									))
									.should(s -> s.match(m -> m
										.field("productName")
										.query(keyword)
										.operator(Operator.Or)
									))
									.should(s -> s.match(m -> m
										.field("shoppingMall")
										.query(keyword)
										.operator(Operator.Or)
									))
									.minimumShouldMatch("1") // 최소 하나라도 일치해야 함
								)
							);
						}

						// ✅ 이벤트 상태 필터 적용 (Filter Context)
						if (eventStatusName != null) {
							LocalDateTime now = LocalDateTime.now();
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

							switch (eventStatusName) {
								case "전체" -> {
									// "전체"는 모든 데이터를 포함하므로 필터 적용 X
								}

								case "예정" -> boolQuery.filter(f -> f
									.range(r -> r
										.field("startTime")
										.gt(JsonData.of(now.format(formatter))) // 현재 시간보다 이후
									)
								);

								case "진행중" -> boolQuery.filter(f -> f
									.bool(b -> b
										.should(s -> s
											.bool(innerB -> innerB
												.mustNot(m -> m.exists(e -> e.field("endTime"))) // endTime이 null인 경우
											)
										)
										.should(s -> s
											.bool(innerB -> innerB
												.must(s1 -> s1
													.range(r -> r
															.field("startTime")
															.lte(JsonData.of(now.format(formatter)))
														// 시작 시간이 현재 시간보다 이전 or 같음
													)
												)
												.must(s2 -> s2
													.range(r -> r
														.field("endTime")
														.gt(JsonData.of(now.format(formatter))) // 종료 시간이 현재 시간보다 이후
													)
												)
											)
										)
										.minimumShouldMatch("1") // 둘 중 하나라도 만족하면 검색
									)
								);

								case "마감" -> boolQuery.filter(f -> f
									.range(r -> r
										.field("endTime")
										.lt(JsonData.of(now.format(formatter))) // 종료 시간이 현재 시간보다 이전
									)
								);

								default -> {
									// 잘못된 값이 들어왔을 경우 필터 적용 X
								}

							}

						}

						return boolQuery;
					})
				)
				.from((int)newPageable.getOffset())
				.size(newPageable.getPageSize())
				.sort(sort2 -> {
					for (Sort.Order order : sort) {
						sort2.field(f -> f
							.field(order.getProperty()) // ✅ 정렬 기준 필드 적용
							.order(order.isDescending() ? SortOrder.Desc : SortOrder.Asc) // ✅ 정렬 방향 적용
						);
					}
					return sort2;
				});

			SearchResponse<SearchPostsResponse> searchResponse =
				elasticsearchClient.search(searchRequestBuilder.build(), SearchPostsResponse.class);

			List<SearchPostsResponse> results = searchResponse.hits().hits().stream()
				.map(Hit::source)
				.toList();

			// 정보 추가
			results = results.stream().map(SearchPostsResponse::addInfo).toList();

			long totalHits = searchResponse.hits().total() != null ? searchResponse.hits().total().value() : 0;

			return new PageImpl<>(results, newPageable, totalHits);

		} catch (IOException e) {
			throw new ElasticsearchException(e);
		}
	}
}