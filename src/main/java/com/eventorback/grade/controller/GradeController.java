package com.eventorback.grade.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.grade.domain.dto.GradeDto;
import com.eventorback.grade.service.GradeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/grades")
public class GradeController {
	private final GradeService gradeService;

	@AuthorizeRole("admin")
	@GetMapping
	public ResponseEntity<ApiResponse<List<GradeDto>>> getGrades() {
		return ApiResponse.createSuccess(gradeService.getGrades());
	}

	@AuthorizeRole("admin")
	@GetMapping("/paging")
	public ResponseEntity<ApiResponse<Page<GradeDto>>> getGrades(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(gradeService.getGrades(pageable));
	}

	@GetMapping("/{gradeId}")
	public ResponseEntity<ApiResponse<GradeDto>> getGrade(@PathVariable Long gradeId) {
		return ApiResponse.createSuccess(gradeService.getGrade(gradeId));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createGrade(
		@RequestBody GradeDto request) {
		gradeService.createGrade(request);
		return ApiResponse.createSuccess("등급이 추가 되었습니다.");
	}

	@PutMapping("/{gradeId}")
	public ResponseEntity<ApiResponse<Void>> updateGrade(@PathVariable Long gradeId,
		@RequestBody GradeDto request) {
		gradeService.updateGrade(gradeId, request);
		return ApiResponse.createSuccess("등급이 수정 되었습니다.");
	}

	@DeleteMapping("/{gradeId}")
	public ResponseEntity<ApiResponse<Void>> deleteGrade(@PathVariable Long gradeId) {
		gradeService.deleteGrade(gradeId);
		return ApiResponse.createSuccess("등급이 삭제 되었습니다.");
	}
}
