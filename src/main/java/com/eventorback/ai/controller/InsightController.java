package com.eventorback.ai.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.ai.service.InsightPipeline;
import com.eventorback.ai.service.InsightQueryService;
import com.eventorback.ai.service.LinkPreviewService;
import com.eventorback.ai.service.WeeklyReportService;
import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.llm.LlmService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InsightController {
	private final InsightQueryService queries;
	private final InsightPipeline pipeline;
	private final WeeklyReportService reports;
	private final LinkPreviewService linkPreview;
	private final LlmService llm;

	// ===== 공개 =====

	@GetMapping("/api/posts/{postId}/insight")
	public ResponseEntity<ApiResponse<Map<String, Object>>> postInsight(@PathVariable Long postId) {
		return ApiResponse.createSuccess(queries.postInsight(postId));
	}

	@GetMapping("/api/insights/overview")
	public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
		return ApiResponse.createSuccess(queries.overview());
	}

	@GetMapping("/api/brands")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> brands(@RequestParam(defaultValue = "40") int limit) {
		return ApiResponse.createSuccess(queries.brands(limit));
	}

	@GetMapping("/api/brands/{name}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> brand(@PathVariable String name) {
		return ApiResponse.createSuccess(queries.brand(name));
	}

	// ===== 회원: 링크로 채우기 =====

	public record LinkPreviewRequest(String url) {
	}

	@AuthorizeRole("member")
	@PostMapping("/api/ai/link-preview")
	public ResponseEntity<ApiResponse<Map<String, Object>>> linkPreview(@CurrentUserId Long userId,
		@RequestBody LinkPreviewRequest request) {
		return ApiResponse.createSuccess(linkPreview.preview(userId, request.url()));
	}

	// ===== 관리자 =====

	@AuthorizeRole("admin")
	@GetMapping("/api/admin/ai/status")
	public ResponseEntity<ApiResponse<Map<String, Object>>> status() {
		return ApiResponse.createSuccess(Map.of(
			"enabled", llm.enabled(), "jobsEnabled", llm.jobsEnabled(),
			"usage", llm.usageToday(), "backlog", pipeline.backlog(),
			"report", queries.latestReport() == null ? Map.of() : queries.latestReport()));
	}

	public record RunRequest(String task, Integer requests) {
	}

	/** 관리자가 지금 바로 처리(이벤터 몫 안에서). requests 는 최대 30 */
	@AuthorizeRole("admin")
	@PostMapping("/api/admin/ai/run")
	public ResponseEntity<ApiResponse<Object>> run(@RequestBody RunRequest request) {
		int n = Math.min(Math.max(request.requests() == null ? 1 : request.requests(), 1), 30);
		Object result = switch (request.task() == null ? "" : request.task()) {
			case "classify" -> pipeline.classifyPending(n);
			case "deep" -> pipeline.deepPending(n);
			case "report" -> reports.generate(true).orElse(null);
			default -> throw new com.eventorback.global.exception.BadRequestException("task 는 classify, deep, report 중 하나예요.");
		};
		return ApiResponse.createSuccess(result);
	}
}
