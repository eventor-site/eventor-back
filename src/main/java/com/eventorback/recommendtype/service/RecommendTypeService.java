package com.eventorback.recommendtype.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.recommendtype.domain.dto.RecommendTypeDto;
import com.eventorback.recommendtype.domain.entity.RecommendType;

public interface RecommendTypeService {

	List<RecommendTypeDto> getRecommendTypes();

	Page<RecommendTypeDto> getRecommendTypes(Pageable pageable);

	RecommendTypeDto getRecommendType(Long recommendTypeId);

	void createRecommendType(RecommendTypeDto request);

	void updateRecommendType(Long recommendId, RecommendTypeDto request);

	void deleteRecommendType(Long recommendId);

	RecommendType findOrCreateRecommendType(String recommendTypeName);
}
