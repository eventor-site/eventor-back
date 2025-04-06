package com.eventorback.statistic.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.global.dto.ApiResponse;
import com.eventorback.statistic.domain.dto.response.GetStatistic;
import com.eventorback.statistic.service.StatisticService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/statistics")
public class StatisticController {
	private final StatisticService statusService;

	@GetMapping
	public ResponseEntity<ApiResponse<Page<GetStatistic>>> getStatistics(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(statusService.getStatistics(pageable));
	}

	@PostMapping("/visitors")
	public ResponseEntity<ApiResponse<Void>> saveVisitor(@RequestParam String uuid) {
		statusService.saveVisitor(uuid);
		return ApiResponse.createSuccess();
	}

}
