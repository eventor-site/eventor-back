package com.eventorback.status.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.status.domain.dto.response.GetStatusResponse;
import com.eventorback.status.domain.entity.Status;

public interface StatusCustomRepository {

	Page<GetStatusResponse> getStatuses(Pageable pageable);

	Optional<GetStatusResponse> getStatus(Long statusId);

	Status findOrCreateStatus(String statusTypeName, String statusName);
}
