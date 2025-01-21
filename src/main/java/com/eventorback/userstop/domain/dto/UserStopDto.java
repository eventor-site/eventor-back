package com.eventorback.userstop.domain.dto;

import com.eventorback.userstop.domain.entity.UserStop;

import lombok.Builder;

@Builder
public record UserStopDto(
	String identifier,
	Long reportTypeId,
	Long stopDay) {

	public static UserStopDto fromEntity(UserStop userStop) {
		return UserStopDto.builder()
			.identifier(userStop.getUser().getIdentifier())
			.reportTypeId(userStop.getReportType().getReportTypeId())
			.stopDay(userStop.getStopDay())
			.build();
	}
}
