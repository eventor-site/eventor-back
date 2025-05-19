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
import com.eventorback.search.service.impl.KeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/searches")
public class SearchController {
	private final KeywordService keywordService;

	@AuthorizeRole("admin")
	@GetMapping("/keywords")
	public ResponseEntity<ApiResponse<List<String>>> getKeywords() {
		return ApiResponse.createSuccess(keywordService.getKeywords());
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/keywords")
	ResponseEntity<ApiResponse<Void>> deleteKeyword(@RequestParam String keyword) {
		keywordService.deleteKeyword(keyword);
		return ApiResponse.createSuccess("인기 검색어가 삭제 되었습니다.");
	}

}
