package com.eventorback.status.repository;

import java.util.List;

import com.eventorback.status.domain.dto.response.GetStatusResponse;

public interface CustomStatusRepository {

	List<GetStatusResponse> getStatusesByStatusTypeName(String statusTypeName);
}
