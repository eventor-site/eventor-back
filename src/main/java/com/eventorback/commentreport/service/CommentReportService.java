package com.eventorback.commentreport.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;

public interface CommentReportService {

	List<GetCommentReportResponse> getCommentReports();

	Page<GetCommentReportResponse> getCommentReports(Pageable pageable);

	String createCommentReport(Long userId, Long commentId, String reportTypeName);

	String deleteCommentReport(Long userId, Long commentReportId);
}
