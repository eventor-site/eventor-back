package com.eventorback.postreport.domain.dto.request;

import lombok.Builder;

@Builder
public record CreatePostReportRequest(
	Long postId,
	String reportType) {
}
