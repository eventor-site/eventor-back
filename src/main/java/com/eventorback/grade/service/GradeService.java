package com.eventorback.grade.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.grade.domain.dto.GradeDto;

public interface GradeService {

	Page<GradeDto> getGrades(Pageable pageable);

	GradeDto getGrade(Long getGradeId);

	void createGrade(GradeDto request);

	void updateGrade(Long gradeId, GradeDto request);

	void deleteGrade(Long gradeId);
}
