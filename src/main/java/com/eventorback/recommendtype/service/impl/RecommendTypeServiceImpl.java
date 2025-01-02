package com.eventorback.recommendtype.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.recommendtype.domain.dto.RecommendTypeDto;
import com.eventorback.recommendtype.domain.entity.RecommendType;
import com.eventorback.recommendtype.exception.RecommendTypeAlreadyExistsException;
import com.eventorback.recommendtype.exception.RecommendTypeNotFoundException;
import com.eventorback.recommendtype.repository.RecommendTypeRepository;
import com.eventorback.recommendtype.service.RecommendTypeService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class RecommendTypeServiceImpl implements RecommendTypeService {
	private final RecommendTypeRepository recommendTypeRepository;

	@Override
	@Transactional(readOnly = true)
	public List<RecommendTypeDto> getRecommendTypes() {
		return recommendTypeRepository.findAll().stream().map(RecommendTypeDto::fromEntity).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RecommendTypeDto> getRecommendTypes(Pageable pageable) {
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public RecommendTypeDto getRecommendType(Long recommendTypeId) {
		RecommendType recommendType = recommendTypeRepository.findById(recommendTypeId)
			.orElseThrow(() -> new RecommendTypeNotFoundException(recommendTypeId));
		return RecommendTypeDto.fromEntity(recommendType);
	}

	@Override
	public void createRecommendType(RecommendTypeDto request) {
		if (recommendTypeRepository.existsByName(request.name())) {
			throw new RecommendTypeAlreadyExistsException(request.name());
		}
		recommendTypeRepository.save(RecommendType.toEntity(request));
	}

	@Override
	public void updateRecommendType(Long recommendId, RecommendTypeDto request) {
		RecommendType recommendType = recommendTypeRepository.findById(recommendId)
			.orElseThrow(() -> new RecommendTypeNotFoundException(recommendId));

		if (recommendTypeRepository.existsByName(request.name())) {
			throw new RecommendTypeAlreadyExistsException(request.name());
		}

		recommendType.updateRecommendType(request.name());
	}

	@Override
	public void deleteRecommendType(Long recommendTypeId) {
		recommendTypeRepository.deleteById(recommendTypeId);
	}

	@Override
	public RecommendType findOrCreateRecommendType(String recommendTypeName) {
		return recommendTypeRepository.findByName(recommendTypeName)
			.orElseGet(() -> recommendTypeRepository.save(RecommendType.toEntityFindOrCreate(recommendTypeName)));
	}

}
