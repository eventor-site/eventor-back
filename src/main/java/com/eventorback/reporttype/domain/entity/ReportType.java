package com.eventorback.reporttype.domain.entity;

import com.eventorback.reporttype.domain.dto.ReportTypeDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class ReportType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_type_id")
	private Long reportTypeId;

	@Column(name = "name")
	private String name;

	@Column(name = "day")
	private Long day;

	@Builder
	public ReportType(String name, Long day) {
		this.name = name;
		this.day = day;
	}

	public static ReportType toEntity(ReportTypeDto request) {
		return ReportType.builder()
			.name(request.name())
			.day(request.day())
			.build();
	}

	public void updateReportType(ReportTypeDto request) {
		this.name = request.name();
		this.day = request.day();
	}

}
