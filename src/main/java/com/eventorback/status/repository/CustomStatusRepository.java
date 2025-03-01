package com.eventorback.status.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.status.domain.dto.response.GetStatusResponse;
import com.eventorback.status.domain.entity.Status;

public interface CustomStatusRepository {

	List<GetStatusResponse> getStatuses(String statusType);

	Page<GetStatusResponse> getStatuses(Pageable pageable);

	Optional<Status> getStatus(Long statusId);

	Optional<GetStatusResponse> getStatusDto(Long statusId);

	Status findOrCreateStatus(String statusTypeName, String statusName);
}
