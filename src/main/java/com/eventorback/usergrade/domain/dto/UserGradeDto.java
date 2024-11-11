package com.eventorback.usergrade.domain.dto;

import java.math.BigDecimal;

import com.eventorback.usergrade.domain.entity.UserGrade;

import lombok.Builder;

@Builder
public record UserGradeDto(
	Long userGradeId,
	String name,
	BigDecimal minAmount,
	BigDecimal maxAmount,
	BigDecimal pointRate) {

	public static UserGradeDto fromEntity(UserGrade userGrade) {
		return UserGradeDto.builder()
			.userGradeId(userGrade.getUserGradeId())
			.name(userGrade.getName())
			.minAmount(userGrade.getMinAmount())
			.maxAmount(userGrade.getMaxAmount())
			.pointRate(userGrade.getPointRate())
			.build();
	}
}
