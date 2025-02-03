package com.eventorback.grade.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.grade.domain.dto.GradeDto;

public interface CustomGradeRepository {

	Page<GradeDto> getGrades(Pageable pageable);
}
