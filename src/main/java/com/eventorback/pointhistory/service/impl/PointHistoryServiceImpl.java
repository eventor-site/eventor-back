package com.eventorback.pointhistory.service.impl;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.pointhistory.domain.dto.response.GetUserPointTotalResponse;
import com.eventorback.pointhistory.repository.PointHistoryRepository;
import com.eventorback.pointhistory.service.PointHistoryService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PointHistoryServiceImpl implements PointHistoryService {
	private final PointHistoryRepository pointHistoryRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<GetUserPointTotalResponse> getUserPointTotalsByPeriod(LocalDateTime startTime, LocalDateTime endTime,
		Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return pointHistoryRepository.getUserPointTotalsByPeriod(startTime, endTime, PageRequest.of(page, pageSize));
	}

}
