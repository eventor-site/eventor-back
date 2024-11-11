package com.eventorback.statustype.domain.dto;

import com.sikyeojoback.statustype.domain.entity.StatusType;

import lombok.Builder;

@Builder
public record StatusTypeDto(
	Long statusTypeId,
	String name) {

	public static StatusTypeDto fromEntity(StatusType statusType) {
		return StatusTypeDto.builder()
			.statusTypeId(statusType.getStatusTypeId())
			.name(statusType.getName())
			.build();
	}
}
