package com.eventorback.grade.domain.dto;

import com.eventorback.grade.domain.entity.Grade;

import lombok.Builder;

@Builder
public record GradeDto(
	Long gradeId,
	String name,
	Long minAmount,
	Long maxAmount) {

	public static GradeDto fromEntity(Grade grade) {
		return GradeDto.builder()
			.gradeId(grade.getGradeId())
			.name(grade.getName())
			.minAmount(grade.getMinAmount())
			.maxAmount(grade.getMaxAmount())
			.build();
	}
}
