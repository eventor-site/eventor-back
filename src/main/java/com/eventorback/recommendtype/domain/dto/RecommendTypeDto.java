package com.eventorback.recommendtype.domain.dto;

import com.eventorback.recommendtype.domain.entity.RecommendType;

import lombok.Builder;

@Builder
public record RecommendTypeDto(
	Long recommendTypeId,
	String name) {

	public static RecommendTypeDto fromEntity(RecommendType recommendType) {
		return RecommendTypeDto.builder()
			.recommendTypeId(recommendType.getRecommendTypeId())
			.name(recommendType.getName())
			.build();
	}
}
