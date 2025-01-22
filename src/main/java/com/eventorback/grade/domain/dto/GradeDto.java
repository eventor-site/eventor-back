package com.eventorback.grade.domain.dto;

import java.math.BigDecimal;

import com.eventorback.grade.domain.entity.Grade;

import lombok.Builder;

@Builder
public record GradeDto(
	Long gradeId,
	String name,
	BigDecimal minAmount,
	BigDecimal maxAmount,
	BigDecimal pointRate) {

	public static GradeDto fromEntity(Grade grade) {
		return GradeDto.builder()
			.gradeId(grade.getGradeId())
			.name(grade.getName())
			.minAmount(grade.getMinAmount())
			.maxAmount(grade.getMaxAmount())
			.pointRate(grade.getPointRate())
			.build();
	}
}
