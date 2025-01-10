package com.eventorback.reporttype.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.reporttype.domain.dto.ReportTypeDto;
import com.eventorback.reporttype.domain.entity.ReportType;
import com.eventorback.reporttype.exception.ReportTypeAlreadyExistsException;
import com.eventorback.reporttype.exception.ReportTypeNotFoundException;
import com.eventorback.reporttype.repository.ReportTypeRepository;
import com.eventorback.reporttype.service.ReportTypeService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportTypeServiceImpl implements ReportTypeService {
	private final ReportTypeRepository reportTypeRepository;

	@Override
	@Transactional(readOnly = true)
	public List<ReportTypeDto> getReportTypes() {
		return reportTypeRepository.findAll().stream().map(ReportTypeDto::fromEntity).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReportTypeDto> getReportTypes(Pageable pageable) {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public ReportTypeDto getReportType(Long reportTypeId) {
		ReportType reportType = reportTypeRepository.findById(reportTypeId)
			.orElseThrow(() -> new ReportTypeNotFoundException(reportTypeId));
		return ReportTypeDto.fromEntity(reportType);
	}

	@Override
	public void createReportType(ReportTypeDto request) {
		if (reportTypeRepository.existsByName(request.name())) {
			throw new ReportTypeAlreadyExistsException(request.name());
		}
		reportTypeRepository.save(ReportType.toEntity(request));
	}

	@Override
	public void updateReportType(Long reportId, ReportTypeDto request) {
		ReportType reportType = reportTypeRepository.findById(reportId)
			.orElseThrow(() -> new ReportTypeNotFoundException(reportId));

		if (reportTypeRepository.existsByName(request.name())) {
			throw new ReportTypeAlreadyExistsException(request.name());
		}

		reportType.updateReportType(request.name());
	}

	@Override
	public void deleteReportType(Long reportTypeId) {
		reportTypeRepository.deleteById(reportTypeId);
	}

}
