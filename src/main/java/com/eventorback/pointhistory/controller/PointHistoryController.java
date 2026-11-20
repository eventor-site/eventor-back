package com.eventorback.pointhistory.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.pointhistory.domain.dto.response.GetUserPointTotalResponse;
import com.eventorback.pointhistory.service.PointHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pointHistories")
public class PointHistoryController {
	private final PointHistoryService pointHistoryService;

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<ApiResponse<Page<GetUserPointTotalResponse>>> getUserPointTotalsByPeriod(
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
		@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		// 기간을 주지 않으면 이번 달 1일부터 지금까지
		LocalDateTime from = startTime != null ? startTime : LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
		LocalDateTime to = endTime != null ? endTime : LocalDateTime.now();
		return ApiResponse.createSuccess(pointHistoryService.getUserPointTotalsByPeriod(from, to, pageable));
	}

}
