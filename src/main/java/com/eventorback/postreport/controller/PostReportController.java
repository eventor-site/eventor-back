package com.eventorback.postreport.controller;

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
import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;
import com.eventorback.postreport.service.PostReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class PostReportController {
	private final PostReportService postReportService;

	@GetMapping("/postReports")
	public ResponseEntity<List<GetPostReportResponse>> getPostReports() {
		return ResponseEntity.status(HttpStatus.OK).body(postReportService.getPostReports());
	}

	@PostMapping("/posts/{postId}/postReports")
	public ResponseEntity<String> createPostReport(@CurrentUserId Long userId, @PathVariable Long postId,
		@RequestParam String reportTypeName) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(postReportService.createPostReport(userId, postId, reportTypeName));
	}

	@DeleteMapping("/postReports/{postReportId}")
	public ResponseEntity<String> deletePostReport(@CurrentUserId Long userId, @PathVariable Long postReportId) {
		return ResponseEntity.status(HttpStatus.OK).body(postReportService.deletePostReport(userId, postReportId));
	}
}
