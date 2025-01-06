package com.eventorback.status.repository;

import java.util.List;

import com.eventorback.status.domain.dto.response.GetStatusResponse;
import com.eventorback.status.domain.entity.Status;

public interface CustomStatusRepository {

	List<GetStatusResponse> getStatusesByStatusTypeName(String statusTypeName);

	Status findOrCreateStatus(String statusTypeName, String statusName);
}
