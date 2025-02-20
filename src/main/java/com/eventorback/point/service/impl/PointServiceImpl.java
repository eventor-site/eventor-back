package com.eventorback.point.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.point.domain.dto.request.PointRequest;
import com.eventorback.point.domain.dto.response.GetPointResponse;
import com.eventorback.point.domain.entity.Point;
import com.eventorback.point.exception.PointAlreadyExistsException;
import com.eventorback.point.exception.PointNotFoundException;
import com.eventorback.point.repository.PointRepository;
import com.eventorback.point.service.PointService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
	private final PointRepository pointRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<GetPointResponse> getPoints(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return pointRepository.getPoints(PageRequest.of(page, pageSize));
	}

	@Override
	@Transactional(readOnly = true)
	public GetPointResponse getPoint(Long pointId) {
		return pointRepository.getPoint(pointId).orElseThrow(PointNotFoundException::new);
	}

	@Override
	public void createPoint(PointRequest request) {
		if (pointRepository.existsByName(request.name())) {
			throw new PointAlreadyExistsException();
		}

		pointRepository.save(Point.toEntity(request));
	}

	@Override
	public void updatePoint(Long pointId, PointRequest request) {
		Point point = pointRepository.findById(pointId).orElseThrow(PointNotFoundException::new);

		if (pointRepository.existsByName(request.name()) && !point.getName().equals(request.name())) {
			throw new PointAlreadyExistsException();
		}

		point.update(request);
	}

	@Override
	public void deletePoint(Long pointId) {
		pointRepository.deleteById(pointId);
	}

}
