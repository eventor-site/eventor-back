package com.eventorback.search.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.global.dto.ApiResponse;
import com.eventorback.search.service.impl.KeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/searches")
public class SearchController {
	private final KeywordService keywordService;

	@GetMapping("/topKeywords")
	public ResponseEntity<ApiResponse<List<String>>> getTopKeywords() {
		return ApiResponse.createSuccess(keywordService.getTopKeywords());
	}

}
