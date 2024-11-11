package com.eventorback.status.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.status.domain.dto.request.StatusRequest;
import com.eventorback.status.domain.dto.response.GetStatusResponse;

public interface StatusService {

	List<GetStatusResponse> getStatusesByStatusTypeName(String statusTypeName);

	List<GetStatusResponse> getStatuses();

	Page<GetStatusResponse> getStatuses(Pageable pageable);

	GetStatusResponse getStatus(Long statusId);

	void createStatus(StatusRequest request);

	void updateStatus(Long statusId, StatusRequest request);

	void deleteStatus(Long statusId);
}
