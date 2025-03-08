package com.eventorback.postreport.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;
import com.eventorback.postreport.service.PostReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class PostReportController {
	private final PostReportService postReportService;

	@AuthorizeRole("admin")
	@GetMapping("/postReports/paging")
	public ResponseEntity<ApiResponse<Page<GetPostReportResponse>>> getPostReports(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(postReportService.getPostReports(pageable));
	}

	@PostMapping("/posts/{postId}/postReports")
	public ResponseEntity<ApiResponse<Void>> createPostReport(@CurrentUserId Long userId, @PathVariable Long postId,
		@RequestParam String reportTypeName) {
		return ApiResponse.createSuccess(postReportService.createPostReport(userId, postId, reportTypeName));
	}

	@GetMapping("/posts/{postId}/postReports/{postReportId}/confirm")
	ResponseEntity<ApiResponse<Void>> confirmPostReport(@PathVariable Long postId, @PathVariable Long postReportId) {
		postReportService.confirmPostReport(postReportId);
		return ApiResponse.createSuccess("신고 게시물을 확인 하였습니다.");
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/postReports/{postReportId}")
	public ResponseEntity<ApiResponse<Void>> deletePostReport(@CurrentUserId Long userId,
		@PathVariable Long postReportId) {
		postReportService.deletePostReport(userId, postReportId);
		return ApiResponse.createSuccess("게시물 신고가 삭제 되었습니다.");
	}
}
