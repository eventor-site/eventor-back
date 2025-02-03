package com.eventorback.status.controller;

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

import com.eventorback.status.domain.dto.request.StatusRequest;
import com.eventorback.status.domain.dto.response.GetStatusResponse;
import com.eventorback.status.service.StatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/statuses")
public class StatusController {
	private final StatusService statusService;

	@GetMapping("/paging")
	public ResponseEntity<Page<GetStatusResponse>> getStatuses(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(statusService.getStatuses(pageable));
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
