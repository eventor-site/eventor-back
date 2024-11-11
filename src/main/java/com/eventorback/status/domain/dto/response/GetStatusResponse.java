package com.eventorback.status.domain.dto.response;

import com.sikyeojoback.status.domain.entity.Status;

import lombok.Builder;

@Builder
public record GetStatusResponse(
	Long statusId,
	String name,
	String statusTypeName) {
	public static GetStatusResponse fromEntity(Status status) {
		return GetStatusResponse.builder()
			.statusId(status.getStatusId())
			.name(status.getName())
			.statusTypeName(status.getStatusType().getName())
			.build();
	}
}
