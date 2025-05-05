package com.eventorback.pointhistory.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.pointhistory.domain.dto.response.GetUserPointTotalResponse;

public interface PointHistoryService {

	Page<GetUserPointTotalResponse> getUserPointTotalsByPeriod(LocalDateTime startDate, LocalDateTime endDate,
		Pageable pageable);
}
