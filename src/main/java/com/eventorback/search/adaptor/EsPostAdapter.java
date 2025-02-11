// package com.eventorback.search.adaptor;
//
// import org.elasticsearch.index.query.MultiMatchQueryBuilder;
// import org.elasticsearch.index.query.QueryBuilders;
// import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.elasticsearch.client.elc.NativeQuery;
// import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
// import org.springframework.data.elasticsearch.core.SearchHits;
// import org.springframework.data.elasticsearch.core.query.Query;
// import org.springframework.stereotype.Component;
//
// import com.eventorback.search.domain.EsPost;
//
// import lombok.RequiredArgsConstructor;
//
// @Component
// @RequiredArgsConstructor
// public class EsPostAdapter {
// 	private final ElasticsearchOperations operations;
//
// 	public SearchHits<EsPost> titleContentSearch(String keyword, Pageable pageable) {
//
// 		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "content");
//
// 		HighlightBuilder highlightBuilder = new HighlightBuilder();
// 		highlightBuilder.field("title"); // 하이라이팅을 적용할 필드 지정
// 		highlightBuilder.field("content"); // 하이라이팅을 적용할 필드 지정
// 		highlightBuilder.preTags("<em>"); // 하이라이트 시작 태그
// 		highlightBuilder.postTags("</em>"); // 하이라이트 종료 태그
//
// 		Query query = NativeQuery.builder()
// 			.withQuery(multiMatchQueryBuilder)
// 			.withHighlightBuilder(highlightBuilder) // 하이라이트 설정 추가
// 			.withPageable(pageable)
// 			.build();
//
// 		return operations.search(query, EsPost.class);
// 	}
//
// }