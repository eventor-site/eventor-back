package com.eventorback.grade.domain.entity;

import java.math.BigDecimal;

import com.eventorback.grade.domain.dto.GradeDto;

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
public class Grade {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grade_id")
	private Long gradeId;

	@Column(name = "name")
	private String name;

	@Column(name = "min_amount")
	private BigDecimal minAmount;

	@Column(name = "max_amount")
	private BigDecimal maxAmount;

	@Builder
	public Grade(String name, BigDecimal minAmount, BigDecimal maxAmount) {
		this.name = name;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
	}

	public static Grade toEntity(GradeDto request) {
		return Grade.builder()
			.name(request.name())
			.minAmount(request.minAmount())
			.maxAmount(request.maxAmount())
			.build();
	}

	public void updateGrade(GradeDto request) {
		this.name = request.name();
		this.minAmount = request.minAmount();
		this.maxAmount = request.maxAmount();
	}
}
