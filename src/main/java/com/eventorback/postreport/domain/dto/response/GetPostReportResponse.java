package com.eventorback.postreport.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetPostReportResponse(
	Long postReportId,
	Long postId,
	Long userId,
	String title,
	boolean isChecked,
	LocalDateTime createdAt,
	String reportTypeName) {
}
