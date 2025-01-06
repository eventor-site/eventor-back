package com.eventorback.statustype.repository;

import java.util.List;

import com.eventorback.statustype.domain.dto.StatusTypeDto;
import com.eventorback.statustype.domain.entity.StatusType;

public interface CustomStatusTypeRepository {

	List<StatusTypeDto> searchStatusTypes(String keyword);

	StatusType findOrCreateStatusType(String statusTypeName);
}
