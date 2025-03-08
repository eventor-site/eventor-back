package com.eventorback.status.service.impl;

import java.util.List;

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
	public List<GetStatusResponse> getStatuses(String statusTypeName) {
		return statusRepository.getStatuses(statusTypeName);
	}

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
		return statusRepository.getStatusDto(statusId).orElseThrow(StatusNotFoundException::new);
	}

	@Override
	public void createStatus(StatusRequest request) {
		if (statusRepository.existsByStatusTypeStatusTypeIdAndName(request.statusTypeId(), request.name())) {
			throw new StatusAlreadyExistsException();
		}

		StatusType statusType = statusTypeRepository.findById(request.statusTypeId())
			.orElseThrow(StatusTypeNotFoundException::new);

		statusRepository.save(Status.toEntity(statusType, request));
	}

	@Override
	public void updateStatus(Long statusId, StatusRequest request) {
		if (statusRepository.existsByStatusIdNotAndNameAndStatusType_StatusTypeId(statusId, request.name(),
			request.statusTypeId())) {
			throw new StatusAlreadyExistsException();
		}

		Status status = statusRepository.findById(statusId).orElseThrow(StatusNotFoundException::new);
		StatusType statusType = statusTypeRepository.findById(request.statusTypeId())
			.orElseThrow(StatusTypeNotFoundException::new);
		status.updateName(statusType, request.name());

	}

	@Override
	public void deleteStatus(Long statusId) {
		statusRepository.deleteById(statusId);
	}

}
