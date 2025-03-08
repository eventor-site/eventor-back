package com.eventorback.status.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.status.domain.dto.request.StatusRequest;
import com.eventorback.status.domain.dto.response.GetStatusResponse;
import com.eventorback.status.service.StatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/statuses")
public class StatusController {
	private final StatusService statusService;

	@AuthorizeRole("admin")
	@GetMapping
	public ResponseEntity<ApiResponse<List<GetStatusResponse>>> getStatuses(
		@RequestParam String statusTypeName) {
		return ApiResponse.createSuccess(statusService.getStatuses(statusTypeName));
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<ApiResponse<Page<GetStatusResponse>>> getStatuses(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(statusService.getStatuses(pageable));
	}

	@GetMapping("/{statusId}")
	public ResponseEntity<ApiResponse<GetStatusResponse>> getStatus(@PathVariable Long statusId) {
		return ApiResponse.createSuccess(statusService.getStatus(statusId));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createStatus(
		@RequestBody StatusRequest request) {
		statusService.createStatus(request);
		return ApiResponse.createSuccess("상태가 생성 되었습니다.");
	}

	@PutMapping("/{statusId}")
	public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long statusId,
		@RequestBody StatusRequest request) {
		statusService.updateStatus(statusId, request);
		return ApiResponse.createSuccess("상태가 수정 되었습니다.");
	}

	@DeleteMapping("/{statusId}")
	public ResponseEntity<ApiResponse<Void>> deleteStatus(@PathVariable Long statusId) {
		statusService.deleteStatus(statusId);
		return ApiResponse.createSuccess("상태가 삭제 되었습니다.");
	}
}
