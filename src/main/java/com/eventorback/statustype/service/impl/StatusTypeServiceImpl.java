package com.eventorback.statustype.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.statustype.domain.dto.StatusTypeDto;
import com.eventorback.statustype.domain.entity.StatusType;
import com.eventorback.statustype.exception.StatusTypeAlreadyExistsException;
import com.eventorback.statustype.exception.StatusTypeNotFoundException;
import com.eventorback.statustype.repository.StatusTypeRepository;
import com.eventorback.statustype.service.StatusTypeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StatusTypeServiceImpl implements StatusTypeService {
	private final StatusTypeRepository statusTypeRepository;

	@Override
	@Transactional(readOnly = true)
	public List<StatusTypeDto> searchStatusTypes(String keyword) {
		return statusTypeRepository.searchStatusTypes(keyword);
	}

	@Override
	@Transactional(readOnly = true)
	public List<StatusTypeDto> getStatusTypes() {
		return statusTypeRepository.getStatusTypes();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<StatusTypeDto> getStatusTypes(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return statusTypeRepository.getStatusTypes(PageRequest.of(page, pageSize));
	}

	@Override
	@Transactional(readOnly = true)
	public StatusTypeDto getStatusType(Long statusTypeId) {
		StatusType statusType = statusTypeRepository.findById(statusTypeId)
			.orElseThrow(StatusTypeNotFoundException::new);
		return StatusTypeDto.fromEntity(statusType);
	}

	@Override
	public void createStatusType(StatusTypeDto request) {
		if (statusTypeRepository.existsByName(request.name())) {
			throw new StatusTypeAlreadyExistsException();
		}
		statusTypeRepository.save(StatusType.toEntity(request));
	}

	@Override
	public void updateStatusType(Long statusId, StatusTypeDto request) {
		StatusType statusType = statusTypeRepository.findById(statusId)
			.orElseThrow(StatusTypeNotFoundException::new);

		if (statusTypeRepository.existsByName(request.name())) {
			throw new StatusTypeAlreadyExistsException();
		}

		statusType.updateStatusType(request.name());
	}

	@Override
	public void deleteStatusType(Long statusTypeId) {
		statusTypeRepository.deleteById(statusTypeId);
	}
}
