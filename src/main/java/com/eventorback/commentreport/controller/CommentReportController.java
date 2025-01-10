package com.eventorback.commentreport.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;
import com.eventorback.commentreport.service.CommentReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class CommentReportController {
	private final CommentReportService commentReportService;

	@GetMapping("/commentReports")
	public ResponseEntity<List<GetCommentReportResponse>> getCommentReports() {
		return ResponseEntity.status(HttpStatus.OK).body(commentReportService.getCommentReports());
	}

	@PostMapping("/comments/{commentId}/commentReports")
	public ResponseEntity<String> createCommentReport(@CurrentUserId Long userId, @PathVariable Long commentId,
		@RequestParam String reportTypeName) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(commentReportService.createCommentReport(userId, commentId, reportTypeName));
	}

	@DeleteMapping("/commentReports/{commentReportId}")
	public ResponseEntity<String> deleteCommentReport(@CurrentUserId Long userId, @PathVariable Long commentReportId) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(commentReportService.deleteCommentReport(userId, commentReportId));
	}
}
