package com.eventorback.commentreport.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;

public interface CommentReportService {

	Page<GetCommentReportResponse> getCommentReports(Pageable pageable);

	String createCommentReport(Long userId, Long commentId, String reportTypeName);

	void confirmCommentReport(Long commentReportId);

	String deleteCommentReport(Long userId, Long commentReportId);
}
