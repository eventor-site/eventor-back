package com.eventorback.reporttype.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.reporttype.domain.dto.ReportTypeDto;

public interface ReportTypeService {

	List<ReportTypeDto> getReportTypes();

	Page<ReportTypeDto> getReportTypes(Pageable pageable);

	ReportTypeDto getReportType(Long reportTypeId);

	void createReportType(ReportTypeDto request);

	void updateReportType(Long reportTypeId, ReportTypeDto request);

	void deleteReportType(Long reportId);
}
