package com.eventorback.usergrade.domain.dto.request;

import java.math.BigDecimal;

public record CreateUserGradeRequest(
	String name,
	BigDecimal minAmount,
	BigDecimal maxAmount,
	BigDecimal pointRate) {
}
