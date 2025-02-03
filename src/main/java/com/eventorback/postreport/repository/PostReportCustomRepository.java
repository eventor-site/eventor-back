package com.eventorback.postreport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.postreport.domain.dto.response.GetPostReportResponse;

public interface PostReportCustomRepository {

	Page<GetPostReportResponse> getPostReports(Pageable pageable);
}