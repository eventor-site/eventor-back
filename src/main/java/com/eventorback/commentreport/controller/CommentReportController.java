package com.eventorback.commentreport.controller;

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
import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;
import com.eventorback.commentreport.service.CommentReportService;
import com.eventorback.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class CommentReportController {
	private final CommentReportService commentReportService;

	@AuthorizeRole("admin")
	@GetMapping("/commentReports/paging")
	public ResponseEntity<ApiResponse<Page<GetCommentReportResponse>>> getCommentReports(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(commentReportService.getCommentReports(pageable));
	}

	@PostMapping("/comments/{commentId}/commentReports")
	public ResponseEntity<ApiResponse<Void>> createCommentReport(@CurrentUserId Long userId,
		@PathVariable Long commentId,
		@RequestParam String reportTypeName) {
		return ApiResponse.createSuccess(commentReportService.createCommentReport(userId, commentId, reportTypeName));
	}

	@GetMapping("/posts/{postId}/comments/{commentId}/commentReports/{commentReportId}/confirm")
	ResponseEntity<ApiResponse<Void>> confirmCommentReport(@PathVariable Long postId, @PathVariable Long commentId,
		@PathVariable Long commentReportId) {
		commentReportService.confirmCommentReport(commentReportId);
		return ApiResponse.createSuccess("신고 댓글을 확인 하였습니다.");
	}

	@DeleteMapping("/commentReports/{commentReportId}")
	public ResponseEntity<ApiResponse<Void>> deleteCommentReport(@CurrentUserId Long userId,
		@PathVariable Long commentReportId) {
		commentReportService.deleteCommentReport(userId, commentReportId);
		return ApiResponse.createSuccess("댓글 신고가 삭제 되었습니다.");
	}
}
