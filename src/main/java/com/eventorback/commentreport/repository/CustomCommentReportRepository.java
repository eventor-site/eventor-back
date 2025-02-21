package com.eventorback.commentreport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.commentreport.domain.dto.response.GetCommentReportResponse;

public interface CustomCommentReportRepository {

	Page<GetCommentReportResponse> getCommentReports(Pageable pageable);
}