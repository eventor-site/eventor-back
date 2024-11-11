package com.eventorback.status.domain.dto.request;

import com.eventorback.status.domain.entity.Status;

import lombok.Builder;

@Builder
public record StatusRequest(
	String name,
	Long statusTypeId) {
	public static StatusRequest fromEntity(Status status) {
		return StatusRequest.builder()
			.name(status.getName())
			.statusTypeId(status.getStatusType().getStatusTypeId())
			.build();
	}
}
