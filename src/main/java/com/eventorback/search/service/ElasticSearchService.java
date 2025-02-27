package com.eventorback.search.service;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.ElasticsearchException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eventorback.search.document.dto.reponse.SearchPostsResponse;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ElasticSearchService {
	private final ElasticsearchClient elasticsearchClient;

	public Page<SearchPostsResponse> searchPosts(Pageable pageable, String keyword) {
		try {
			int page = Math.max(pageable.getPageNumber() - 1, 0);
			int pageSize = pageable.getPageSize();
			Pageable newPageable = PageRequest.of(page, pageSize);

			SearchRequest searchRequest = new SearchRequest.Builder()
				.index("post")
				.query(query -> query
					.bool(boolQuery -> boolQuery
						.should(shouldQuery -> shouldQuery
							.match(match -> match
								.field("title")  // 제목 검색
								.query(keyword)
								.operator(Operator.And)
							)
						)
						.should(shouldQuery -> shouldQuery
							.match(match -> match
								.field("content")  // 내용 검색
								.query(keyword)
								.operator(Operator.And)
							)
						)
						.should(shouldQuery -> shouldQuery
							.match(match -> match
								.field("productName")  // 내용 검색
								.query(keyword)
								.operator(Operator.And)
							)
						)
						.mustNot(mustNotQuery -> mustNotQuery
							.term(t -> t
								.field("statusName")
								.value("삭제됨") // 삭제된 게시물은 제외
							)
						)
					)
				)
				.from((int)newPageable.getOffset())
				.size(newPageable.getPageSize())
				.sort(sort -> sort
					.field(f -> f
						.field("createdAt")
						.order(SortOrder.Desc)
					)
				)
				.build();

			SearchResponse<SearchPostsResponse> searchResponse =
				elasticsearchClient.search(searchRequest, SearchPostsResponse.class);

			List<SearchPostsResponse> results = searchResponse.hits().hits().stream()
				.map(Hit::source)
				.toList();

			long totalHits = searchResponse.hits().total() != null ? searchResponse.hits().total().value() : 0;

			return new PageImpl<>(results, newPageable, totalHits);

		} catch (IOException e) {
			throw new ElasticsearchException(e);
		}
	}
}
