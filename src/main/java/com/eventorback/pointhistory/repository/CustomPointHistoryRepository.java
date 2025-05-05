package com.eventorback.pointhistory.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.pointhistory.domain.dto.response.GetUserPointTotalResponse;

public interface CustomPointHistoryRepository {

	Page<GetUserPointTotalResponse> getUserPointTotalsByPeriod(LocalDateTime startTime, LocalDateTime endTime,
		Pageable pageable);

}
