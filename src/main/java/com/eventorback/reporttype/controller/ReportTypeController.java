package com.eventorback.reporttype.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.reporttype.domain.dto.ReportTypeDto;
import com.eventorback.reporttype.service.ReportTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/reportTypes")
public class ReportTypeController {
	private final ReportTypeService reportTypeService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<ReportTypeDto>>> getReportTypes() {
		return ApiResponse.createSuccess(reportTypeService.getReportTypes());
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<ApiResponse<Page<ReportTypeDto>>> getReportTypes(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(reportTypeService.getReportTypes(pageable));
	}

	@GetMapping("/{reportTypeId}")
	public ResponseEntity<ApiResponse<ReportTypeDto>> getReportType(@PathVariable Long reportTypeId) {
		return ApiResponse.createSuccess(reportTypeService.getReportType(reportTypeId));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createReportType(
		@RequestBody ReportTypeDto request) {
		reportTypeService.createReportType(request);
		return ApiResponse.createSuccess("신고 유형이 생성 되었습니다.");
	}

	@PutMapping("/{reportTypeId}")
	public ResponseEntity<ApiResponse<Void>> updateReportType(@PathVariable Long reportTypeId,
		@RequestBody ReportTypeDto request) {
		reportTypeService.updateReportType(reportTypeId, request);
		return ApiResponse.createSuccess("신고 유형이 수정 되었습니다.");
	}

	@DeleteMapping("/{reportTypeId}")
	public ResponseEntity<ApiResponse<Void>> deleteReportType(@PathVariable Long reportTypeId) {
		reportTypeService.deleteReportType(reportTypeId);
		return ApiResponse.createSuccess("신고 유형이 삭제 되었니다.");
	}
}
