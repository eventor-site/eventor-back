package com.eventorback.commentreport.domain.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record GetCommentReportResponse(
	Long commentReportId,
	Long postId,
	Long commentId,
	Long userId,
	String content,
	boolean isChecked,
	LocalDateTime createdAt,
	String reportTypeName) {
}
