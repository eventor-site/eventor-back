package com.eventorback.statustype.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.statustype.domain.dto.StatusTypeDto;

public interface StatusTypeService {

	List<StatusTypeDto> searchStatusTypes(String name);

	List<StatusTypeDto> getStatusTypes();

	Page<StatusTypeDto> getStatusTypes(Pageable pageable);

	StatusTypeDto getStatusType(Long statusTypeId);

	void createStatusType(StatusTypeDto request);

	void updateStatusType(Long statusId, StatusTypeDto request);

	void deleteStatusType(Long statusId);
}
