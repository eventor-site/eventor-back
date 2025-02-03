package com.eventorback.postreport.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;

public interface PostReportService {

	Page<GetPostReportResponse> getPostReports(Pageable pageable);

	String createPostReport(Long userId, Long postId, String reportTypeName);

	void confirmPostReport(Long postReportId);

	String deletePostReport(Long userId, Long postReportId);
}
