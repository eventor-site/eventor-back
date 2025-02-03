package com.eventorback.statustype.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.statustype.domain.dto.StatusTypeDto;
import com.eventorback.statustype.domain.entity.StatusType;

public interface CustomStatusTypeRepository {

	List<StatusTypeDto> searchStatusTypes(String keyword);

	List<StatusTypeDto> getStatusTypes();

	Page<StatusTypeDto> getStatusTypes(Pageable pageable);

	StatusType findOrCreateStatusType(String statusTypeName);
}
