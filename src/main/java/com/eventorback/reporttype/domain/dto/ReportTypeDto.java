package com.eventorback.reporttype.domain.dto;

import com.eventorback.reporttype.domain.entity.ReportType;

import lombok.Builder;

@Builder
public record ReportTypeDto(
	Long reportTypeId,
	String name) {

	public static ReportTypeDto fromEntity(ReportType reportType) {
		return ReportTypeDto.builder()
			.reportTypeId(reportType.getReportTypeId())
			.name(reportType.getName())
			.build();
	}
}
