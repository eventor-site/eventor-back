package com.eventorback.status.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.status.domain.dto.request.StatusRequest;
import com.eventorback.status.domain.dto.response.GetStatusResponse;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.exception.StatusAlreadyExistsException;
import com.eventorback.status.exception.StatusNotFoundException;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.status.service.StatusService;
import com.eventorback.statustype.domain.entity.StatusType;
import com.eventorback.statustype.exception.StatusTypeNotFoundException;
import com.eventorback.statustype.repository.StatusTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class StatusServiceImpl implements StatusService {
	private final StatusRepository statusRepository;
	private final StatusTypeRepository statusTypeRepository;

	@Override
	@Transactional(readOnly = true)
	public Page<GetStatusResponse> getStatuses(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return statusRepository.getStatuses(PageRequest.of(page, pageSize));
	}

	@Override
	@Transactional(readOnly = true)
	public GetStatusResponse getStatus(Long statusId) {
		return statusRepository.getStatus(statusId).orElseThrow(() -> new StatusNotFoundException(statusId));
	}

	@Override
	public void createStatus(StatusRequest request) {
		if (statusRepository.existsByName(request.name())) {
			throw new StatusAlreadyExistsException(request.name());
		}

		StatusType statusType = statusTypeRepository.findById(request.statusTypeId())
			.orElseThrow(() -> new StatusTypeNotFoundException(request.statusTypeId()));

		statusRepository.save(Status.toEntity(statusType, request));
	}

	@Override
	public void updateStatus(Long statusId, StatusRequest request) {
		if (statusRepository.existsByName(request.name())) {
			throw new StatusAlreadyExistsException(request.name());
		}

		Status status = statusRepository.findById(statusId).orElseThrow(() -> new StatusNotFoundException(statusId));
		StatusType statusType = statusTypeRepository.findById(request.statusTypeId())
			.orElseThrow(() -> new StatusTypeNotFoundException(request.statusTypeId()));
		status.updateName(statusType, request.name());

	}

	@Override
	public void deleteStatus(Long statusId) {
		statusRepository.deleteById(statusId);
	}

}
