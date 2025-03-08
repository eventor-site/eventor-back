package com.eventorback.reporttype.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
		return reportTypeRepository.getReportTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReportTypeDto> getReportTypes(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return reportTypeRepository.getReportTypes(PageRequest.of(page, pageSize));
	}

	@Override
	@Transactional(readOnly = true)
	public ReportTypeDto getReportType(Long reportTypeId) {
		ReportType reportType = reportTypeRepository.findById(reportTypeId)
			.orElseThrow(ReportTypeNotFoundException::new);
		return ReportTypeDto.fromEntity(reportType);
	}

	@Override
	public void createReportType(ReportTypeDto request) {
		if (reportTypeRepository.existsByName(request.name())) {
			throw new ReportTypeAlreadyExistsException();
		}
		reportTypeRepository.save(ReportType.toEntity(request));
	}

	@Override
	public void updateReportType(Long reportTypeId, ReportTypeDto request) {
		if (reportTypeRepository.existsByReportTypeIdNotAndName(reportTypeId, request.name())) {
			throw new ReportTypeAlreadyExistsException();
		}

		ReportType reportType = reportTypeRepository.findById(reportTypeId)
			.orElseThrow(ReportTypeNotFoundException::new);

		reportType.updateReportType(request);
	}

	@Override
	public void deleteReportType(Long reportTypeId) {
		reportTypeRepository.deleteById(reportTypeId);
	}

}
