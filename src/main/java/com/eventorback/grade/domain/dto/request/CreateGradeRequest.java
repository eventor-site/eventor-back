package com.eventorback.grade.domain.dto.request;

import java.math.BigDecimal;

public record CreateGradeRequest(
	String name,
	BigDecimal minAmount,
	BigDecimal maxAmount,
	BigDecimal pointRate) {
}
