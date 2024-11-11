package com.eventorback.status.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.sikyeojoback.status.domain.dto.request.StatusRequest;
import com.sikyeojoback.status.domain.dto.response.GetStatusResponse;
import com.sikyeojoback.status.service.StatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/statuses")
public class StatusController {
	private final StatusService statusService;

	@GetMapping("/types")
	public ResponseEntity<List<GetStatusResponse>> getStatusesByStatusTypeName(@RequestParam String statusTypeName) {
		return ResponseEntity.status(HttpStatus.OK).body(statusService.getStatusesByStatusTypeName(statusTypeName));
	}

	@GetMapping
	public ResponseEntity<List<GetStatusResponse>> getStatuses() {
		return ResponseEntity.status(HttpStatus.OK).body(statusService.getStatuses());
	}

	@GetMapping("/{statusId}")
	public ResponseEntity<GetStatusResponse> getStatus(@PathVariable Long statusId) {
		return ResponseEntity.status(HttpStatus.OK).body(statusService.getStatus(statusId));
	}

	@PostMapping
	public ResponseEntity<Void> createStatus(
		@RequestBody StatusRequest request) {
		statusService.createStatus(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{statusId}")
	public ResponseEntity<Void> updateStatus(@PathVariable Long statusId,
		@RequestBody StatusRequest request) {
		statusService.updateStatus(statusId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{statusId}")
	public ResponseEntity<Void> deleteStatus(@PathVariable Long statusId) {
		statusService.deleteStatus(statusId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
