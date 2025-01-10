package com.eventorback.postreport.repository;

import java.util.List;

import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;

public interface CustomPostReportRepository {
	List<GetPostReportResponse> getPostReports();
}