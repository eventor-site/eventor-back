package com.eventorback.userstop.domain.dto.response;

import java.time.LocalDateTime;

import com.eventorback.userstop.domain.entity.UserStop;

import lombok.Builder;

@Builder
public record GetUserStopResponse(
	Long userStopId,
	String identifier,
	String reportTypeName,
	Long day,
	LocalDateTime startTime,
	LocalDateTime endTime) {

	public static GetUserStopResponse fromEntity(UserStop userStop) {
		return GetUserStopResponse.builder()
			.userStopId(userStop.getUserStopId())
			.identifier(userStop.getUser().getIdentifier())
			.reportTypeName(userStop.getReportType().getName())
			.day(userStop.getReportType().getDay())
			.startTime(userStop.getStartTime())
			.endTime(userStop.getEndTime())
			.build();
	}
}
