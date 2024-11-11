package com.eventorback.usergrade.domain.entity;

import java.math.BigDecimal;

import com.sikyeojoback.usergrade.domain.dto.UserGradeDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class UserGrade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_grade_id")
	private Long userGradeId;

	@Column(name = "name")
	private String name;

	@Column(name = "min_amount")
	private BigDecimal minAmount;

	@Column(name = "max_amount")
	private BigDecimal maxAmount;

	@Column(name = "point_rate")
	private BigDecimal pointRate;

	@Builder
	public UserGrade(String name, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal pointRate) {
		this.name = name;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.pointRate = pointRate;
	}

	public static UserGrade toEntity(UserGradeDto request) {
		return UserGrade.builder()
			.name(request.name())
			.minAmount(request.minAmount())
			.maxAmount(request.maxAmount())
			.pointRate(request.pointRate())
			.build();
	}

	public void updateUserGrade(String name, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal pointRate) {
		this.name = name;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		this.pointRate = pointRate;
	}
}
