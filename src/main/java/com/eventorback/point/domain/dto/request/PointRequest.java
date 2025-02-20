package com.eventorback.point.domain.dto.request;

import com.eventorback.point.domain.entity.Point;

import lombok.Builder;

@Builder
public record PointRequest(
	String name,
	Long amount
) {
	public static PointRequest fromEntity(Point point) {
		return PointRequest.builder()
			.name(point.getName())
			.amount(point.getAmount())
			.build();
	}
}
