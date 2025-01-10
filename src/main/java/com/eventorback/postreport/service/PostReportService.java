package com.eventorback.postreport.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;

public interface PostReportService {

	List<GetPostReportResponse> getPostReports();

	Page<GetPostReportResponse> getPostReports(Pageable pageable);

	String createPostReport(Long userId, Long postId, String reportTypeName);

	String deletePostReport(Long userId, Long postReportId);
}
