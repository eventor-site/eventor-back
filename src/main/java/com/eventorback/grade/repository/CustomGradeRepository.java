package com.eventorback.grade.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.grade.domain.dto.GradeDto;

public interface CustomGradeRepository {

	List<GradeDto> getGrades();

	Page<GradeDto> getGrades(Pageable pageable);
}
