package com.eventorback.statustype.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
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
	public ResponseEntity<List<StatusTypeDto>> searchStatusTypes(@RequestParam String keyword) {
		return ResponseEntity.status(HttpStatus.OK).body(statusTypeService.searchStatusTypes(keyword));
	}

	@GetMapping
	public ResponseEntity<List<StatusTypeDto>> getStatusTypes() {
		return ResponseEntity.status(HttpStatus.OK).body(statusTypeService.getStatusTypes());
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<Page<StatusTypeDto>> getStatusTypes(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(statusTypeService.getStatusTypes(pageable));
	}

	@GetMapping("/{statusTypeId}")
	public ResponseEntity<StatusTypeDto> getStatusType(@PathVariable Long statusTypeId) {
		return ResponseEntity.status(HttpStatus.OK).body(statusTypeService.getStatusType(statusTypeId));
	}

	@PostMapping
	public ResponseEntity<Void> createStatusType(
		@RequestBody StatusTypeDto request) {
		statusTypeService.createStatusType(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{statusTypeId}")
	public ResponseEntity<Void> updateStatusType(@PathVariable Long statusTypeId,
		@RequestBody StatusTypeDto request) {
		statusTypeService.updateStatusType(statusTypeId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{statusTypeId}")
	public ResponseEntity<Void> deleteStatusType(@PathVariable Long statusTypeId) {
		statusTypeService.deleteStatusType(statusTypeId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
