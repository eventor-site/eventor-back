package com.eventorback.userstop.domain.dto;

import com.eventorback.userstop.domain.entity.UserStop;

import lombok.Builder;

@Builder
public record UserStopDto(
	Long userId,
	Long reportTypeId,
	Long stopDay) {

	public static UserStopDto fromEntity(UserStop userStop) {
		return UserStopDto.builder()
			.userId(userStop.getUser().getUserId())
			.reportTypeId(userStop.getReportType().getReportTypeId())
			.stopDay(userStop.getStopDay())
			.build();
	}
}
