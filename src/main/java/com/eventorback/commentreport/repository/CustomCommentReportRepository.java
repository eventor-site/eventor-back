package com.eventorback.commentreport.repository;

import java.util.List;

import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;

public interface CustomCommentReportRepository {
	List<GetCommentReportResponse> getCommentReports();
}