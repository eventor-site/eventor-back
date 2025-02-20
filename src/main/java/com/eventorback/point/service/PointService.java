package com.eventorback.point.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.point.domain.dto.request.PointRequest;
import com.eventorback.point.domain.dto.response.GetPointResponse;

public interface PointService {

	Page<GetPointResponse> getPoints(Pageable pageable);

	GetPointResponse getPoint(Long pointId);

	void createPoint(PointRequest request);

	void updatePoint(Long pointId, PointRequest request);

	void deletePoint(Long pointId);
}
