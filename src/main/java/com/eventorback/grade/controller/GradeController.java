package com.eventorback.grade.controller;

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
import com.eventorback.grade.domain.dto.GradeDto;
import com.eventorback.grade.service.GradeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/grades")
public class GradeController {
	private final GradeService gradeService;

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<Page<GradeDto>> getGrades(@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(gradeService.getGrades(pageable));
	}

	@GetMapping("/{gradeId}")
	public ResponseEntity<GradeDto> getGrade(@PathVariable Long gradeId) {
		return ResponseEntity.status(HttpStatus.OK).body(gradeService.getGrade(gradeId));
	}

	@PostMapping
	public ResponseEntity<Void> createGrade(
		@RequestBody GradeDto request) {
		gradeService.createGrade(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{gradeId}")
	public ResponseEntity<Void> updateGrade(@PathVariable Long gradeId,
		@RequestBody GradeDto request) {
		gradeService.updateGrade(gradeId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{gradeId}")
	public ResponseEntity<Void> deleteGrade(@PathVariable Long gradeId) {
		gradeService.deleteGrade(gradeId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
