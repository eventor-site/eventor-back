package com.eventorback.statustype.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.eventorback.statustype.domain.dto.StatusTypeDto;
import com.eventorback.statustype.service.StatusTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/statusTypes")
public class StatusTypeController {
	private final StatusTypeService statusTypeService;

	@AuthorizeRole("admin")
	@GetMapping("/search")
	public ApiResponse<List<StatusTypeDto>> searchStatusTypes(@RequestParam String keyword) {
		return ApiResponse.createSuccess(statusTypeService.searchStatusTypes(keyword));
	}

	@GetMapping
	public ApiResponse<List<StatusTypeDto>> getStatusTypes() {
		return ApiResponse.createSuccess(statusTypeService.getStatusTypes());
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ApiResponse<Page<StatusTypeDto>> getStatusTypes(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(statusTypeService.getStatusTypes(pageable));
	}

	@GetMapping("/{statusTypeId}")
	public ApiResponse<StatusTypeDto> getStatusType(@PathVariable Long statusTypeId) {
		return ApiResponse.createSuccess(statusTypeService.getStatusType(statusTypeId));
	}

	@PostMapping
	public ApiResponse<Void> createStatusType(
		@RequestBody StatusTypeDto request) {
		statusTypeService.createStatusType(request);
		return ApiResponse.createSuccess("상태 유형이 생성 되었습니다.");
	}

	@PutMapping("/{statusTypeId}")
	public ApiResponse<Void> updateStatusType(@PathVariable Long statusTypeId,
		@RequestBody StatusTypeDto request) {
		statusTypeService.updateStatusType(statusTypeId, request);
		return ApiResponse.createSuccess("상태 유형이 수정 되었습니다.");
	}

	@DeleteMapping("/{statusTypeId}")
	public ApiResponse<Void> deleteStatusType(@PathVariable Long statusTypeId) {
		statusTypeService.deleteStatusType(statusTypeId);
		return ApiResponse.createSuccess("상태 유형이 제거 되었습니다.");
	}
}
