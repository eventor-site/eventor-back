package com.eventorback.point.controller;

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

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.point.domain.dto.request.PointRequest;
import com.eventorback.point.domain.dto.response.GetPointResponse;
import com.eventorback.point.service.PointService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/points")
public class PointController {
	private final PointService pointService;

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<Page<GetPointResponse>> getPoints(@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(pointService.getPoints(pageable));
	}

	@GetMapping("/{pointId}")
	public ResponseEntity<GetPointResponse> getPoint(@PathVariable Long pointId) {
		return ResponseEntity.status(HttpStatus.OK).body(pointService.getPoint(pointId));
	}

	@AuthorizeRole("admin")
	@PostMapping
	public ResponseEntity<Void> createPoint(@RequestBody PointRequest request) {
		pointService.createPoint(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AuthorizeRole("admin")
	@PutMapping("/{pointId}")
	public ResponseEntity<Void> updatePoint(@PathVariable Long pointId, @RequestBody PointRequest request) {
		pointService.updatePoint(pointId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@AuthorizeRole("admin")
	@DeleteMapping("/{pointId}")
	public ResponseEntity<Void> deletePoint(@PathVariable Long pointId) {
		pointService.deletePoint(pointId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
