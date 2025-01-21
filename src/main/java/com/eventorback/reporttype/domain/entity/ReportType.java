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

	@Builder
	public ReportType(String name) {
		this.name = name;
	}

	public static ReportType toEntity(ReportTypeDto request) {
		return ReportType.builder()
			.name(request.name())
			.build();
	}

	public void updateReportType(ReportTypeDto request) {
		this.name = request.name();
	}

}
