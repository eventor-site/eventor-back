package com.eventorback.commentreport.domain.dto.request;

import lombok.Builder;

@Builder
public record CreateCommentReportRequest(
	Long commentId,
	String reportType) {
}
