package com.eventorback.reporttype.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
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
import com.eventorback.reporttype.domain.dto.ReportTypeDto;
import com.eventorback.reporttype.service.ReportTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/reportTypes")
public class ReportTypeController {
	private final ReportTypeService reportTypeService;

	@GetMapping
	public ResponseEntity<List<ReportTypeDto>> getReportTypes() {
		return ResponseEntity.status(HttpStatus.OK).body(reportTypeService.getReportTypes());
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<Page<ReportTypeDto>> getReportTypes(@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(reportTypeService.getReportTypes(pageable));
	}

	@GetMapping("/{reportTypeId}")
	public ResponseEntity<ReportTypeDto> getReportType(@PathVariable Long reportTypeId) {
		return ResponseEntity.status(HttpStatus.OK).body(reportTypeService.getReportType(reportTypeId));
	}

	@PostMapping
	public ResponseEntity<Void> createReportType(
		@RequestBody ReportTypeDto request) {
		reportTypeService.createReportType(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/{reportTypeId}")
	public ResponseEntity<Void> updateReportType(@PathVariable Long reportTypeId,
		@RequestBody ReportTypeDto request) {
		reportTypeService.updateReportType(reportTypeId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{reportTypeId}")
	public ResponseEntity<Void> deleteReportType(@PathVariable Long reportTypeId) {
		reportTypeService.deleteReportType(reportTypeId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
