package com.eventorback.reporttype.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.reporttype.domain.dto.ReportTypeDto;

public interface CustomReportTypeRepository {

	List<ReportTypeDto> getReportTypes();

	Page<ReportTypeDto> getReportTypes(Pageable pageable);
}
