package com.eventorback.usergrade.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.sikyeojoback.usergrade.domain.dto.UserGradeDto;
import com.sikyeojoback.usergrade.service.UserGradeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/user-grades")
public class UserGradeController {
	private final UserGradeService userGradeService;

	@GetMapping
	public ResponseEntity<List<UserGradeDto>> getUserGrades() {
		return ResponseEntity.status(HttpStatus.OK).body(userGradeService.getUserGrades());
	}

	@GetMapping("/{userGradeId}")
	public ResponseEntity<UserGradeDto> getUserGrade(@PathVariable Long userGradeId) {
		return ResponseEntity.status(HttpStatus.OK).body(userGradeService.getUserGrade(userGradeId));
	}

	@PostMapping
	public ResponseEntity<Void> createUserGrade(
		@RequestBody UserGradeDto request) {
		userGradeService.createUserGrade(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{userGradeId}")
	public ResponseEntity<Void> updateUserGrade(@PathVariable Long userGradeId,
		@RequestBody UserGradeDto request) {
		userGradeService.updateUserGrade(userGradeId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{userGradeId}")
	public ResponseEntity<Void> deleteUserGrade(@PathVariable Long userGradeId) {
		userGradeService.deleteUserGrade(userGradeId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
