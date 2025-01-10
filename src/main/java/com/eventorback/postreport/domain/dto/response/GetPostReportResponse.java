package com.eventorback.postreport.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetPostReportResponse(
	Long postReportId,
	Long postId,
	String writer,
	String title,
	LocalDateTime createdAt,
	String reportTypeName) {
}
