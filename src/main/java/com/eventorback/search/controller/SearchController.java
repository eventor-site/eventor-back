package com.eventorback.search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.search.service.KeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/searches")
public class SearchController {
	private final KeywordService keywordService;

	/** 인기 검색어(누구나, 사이드바·검색창) */
	@GetMapping("/keywords/top")
	public ResponseEntity<ApiResponse<List<String>>> getTopKeywords(@RequestParam(defaultValue = "10") int limit) {
		return ApiResponse.createSuccess(keywordService.getKeywords(Math.min(limit, 20)));
	}

	/** 관리자: 인기 검색어 100개 */
	@AuthorizeRole("admin")
	@GetMapping("/keywords")
	public ResponseEntity<ApiResponse<List<String>>> getKeywords() {
		return ApiResponse.createSuccess(keywordService.getKeywords(100));
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/keywords")
	public ResponseEntity<ApiResponse<Void>> deleteKeyword(@RequestParam String keyword) {
		keywordService.deleteKeyword(keyword);
		return ApiResponse.createSuccess("인기 검색어가 삭제 되었습니다.");
	}
}
