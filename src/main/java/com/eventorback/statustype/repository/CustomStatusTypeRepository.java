package com.eventorback.statustype.repository;

import java.util.List;

import com.sikyeojoback.statustype.domain.dto.StatusTypeDto;

public interface CustomStatusTypeRepository {

	List<StatusTypeDto> searchStatusTypes(String keyword);
}
