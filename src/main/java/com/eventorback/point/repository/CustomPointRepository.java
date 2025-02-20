package com.eventorback.point.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.point.domain.dto.response.GetPointResponse;
import com.eventorback.point.domain.entity.Point;

public interface CustomPointRepository {

	Page<GetPointResponse> getPoints(Pageable pageable);

	Optional<GetPointResponse> getPoint(Long pointId);

	Point findOrCreatePoint(String name);
}
