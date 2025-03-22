package com.eventorback.grade.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.grade.domain.dto.GradeDto;
import com.eventorback.grade.domain.entity.Grade;

public interface GradeService {

	List<Grade> findAllByOrderByMinAmountAsc();

	List<GradeDto> getGrades();

	Page<GradeDto> getGrades(Pageable pageable);

	GradeDto getGrade(Long getGradeId);

	void createGrade(GradeDto request);

	void updateGrade(Long gradeId, GradeDto request);

	void deleteGrade(Long gradeId);
}
