package com.eventorback.integration.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.eventorback.integration.domain.dto.IntegrationRequest;
import com.eventorback.integration.domain.dto.IntegrationResponse;
import com.eventorback.integration.service.IntegrationService;

import lombok.RequiredArgsConstructor;

/** 관리자 "외부 연동" 화면. 읽기도 관리자만(어떤 키가 설정됐는지 보이므로). */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/integrations")
public class IntegrationController {
	private final IntegrationService integrationService;

	@AuthorizeRole("admin")
	@GetMapping
	public ResponseEntity<ApiResponse<List<IntegrationResponse>>> getIntegrations() {
		return ApiResponse.createSuccess(integrationService.getIntegrations());
	}

	@AuthorizeRole("admin")
	@PostMapping
	public ResponseEntity<ApiResponse<IntegrationResponse>> create(@Validated @RequestBody IntegrationRequest request) {
		return ApiResponse.createSuccess(integrationService.create(request), "연동을 추가했어요.");
	}

	@AuthorizeRole("admin")
	@PutMapping("/{integrationId}")
	public ResponseEntity<ApiResponse<IntegrationResponse>> update(@PathVariable Long integrationId,
		@Validated @RequestBody IntegrationRequest request) {
		return ApiResponse.createSuccess(integrationService.update(integrationId, request), "저장했어요.");
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/{integrationId}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long integrationId) {
		integrationService.delete(integrationId);
		return ApiResponse.createSuccess("삭제했어요.");
	}

	@AuthorizeRole("admin")
	@PostMapping("/{integrationId}/check")
	public ResponseEntity<ApiResponse<IntegrationResponse>> check(@PathVariable Long integrationId) {
		return ApiResponse.createSuccess(integrationService.check(integrationId));
	}
}
