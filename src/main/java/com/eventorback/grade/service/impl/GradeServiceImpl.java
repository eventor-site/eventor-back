package com.eventorback.grade.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.grade.domain.dto.GradeDto;
import com.eventorback.grade.domain.entity.Grade;
import com.eventorback.grade.exception.GradeAlreadyExistsException;
import com.eventorback.grade.exception.GradeNotFoundException;
import com.eventorback.grade.repository.GradeRepository;
import com.eventorback.grade.service.GradeService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {
	private final GradeRepository gradeRepository;

	@Cacheable(value = "grades", key = "'allGrades'")
	@Override
	public List<Grade> findAllByOrderByMinAmountAsc() {
		return gradeRepository.findAllByOrderByMinAmountAsc();
	}

	@Override
	public List<GradeDto> getGrades() {
		return gradeRepository.getGrades();
	}

	@Override
	public Page<GradeDto> getGrades(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return gradeRepository.getGrades(PageRequest.of(page, pageSize));
	}

	@Override
	public GradeDto getGrade(Long gradeId) {
		return gradeRepository.findById(gradeId)
			.map(GradeDto::fromEntity)
			.orElseThrow(GradeNotFoundException::new);
	}

	@CacheEvict(value = "grades", key = "'allGrades'")
	@Override
	public void createGrade(GradeDto request) {
		if (gradeRepository.existsByName(request.name())) {
			throw new GradeAlreadyExistsException();
		}
		gradeRepository.save(Grade.toEntity(request));
	}

	@CacheEvict(value = "grades", key = "'allGrades'")
	@Override
	public void updateGrade(Long gradeId, GradeDto request) {
		Grade grade = gradeRepository.findById(gradeId)
			.orElseThrow(GradeNotFoundException::new);
		grade.updateGrade(request);
	}

	@CacheEvict(value = "grades", key = "'allGrades'")
	@Override
	public void deleteGrade(Long gradeId) {
		gradeRepository.deleteById(gradeId);
	}
}
