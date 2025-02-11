package com.eventorback.search.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.search.document.dto.reponse.SearchPostsResponse;
import com.eventorback.search.service.ElasticSearchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

	private final ElasticSearchService elasticSearchService;

	@GetMapping("/posts")
	public ResponseEntity<Page<SearchPostsResponse>> searchBooks(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestParam String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(elasticSearchService.searchPosts(pageable, keyword));
	}

	// @GetMapping("/writers")
	// public ResponseEntity<Page<SearchPostsResponse>> searchAuthors(
	// 	@RequestParam String query,
	// 	@PageableDefault(page = 1, size = 20) Pageable pageable) throws IOException {
	// 	return ResponseEntity.status(HttpStatus.OK).body(elasticSearchService.searchWriters(query, pageable));
	// }

	// // 제목+내용+정렬 (통합)
	// @GetMapping("/list")
	// public ResponseEntity<Page<SearchPostsResponse>> titleContentSearch(
	// 	@RequestParam String keyword, @PageableDefault(size = 20) Pageable pageable) {
	// 	return ResponseEntity.ok().body(searchService.searchPost(keyword, pageable));
	// }

}
