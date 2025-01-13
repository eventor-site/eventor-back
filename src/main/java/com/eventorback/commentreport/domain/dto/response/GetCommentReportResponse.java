package com.eventorback.commentreport.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetCommentReportResponse(
	Long commentReportId,
	Long postId,
	Long commentId,
	String identifier,
	String content,
	LocalDateTime createdAt,
	String reportTypeName) {
}
