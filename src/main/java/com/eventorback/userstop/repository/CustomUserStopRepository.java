package com.eventorback.userstop.repository;

import java.util.List;

import com.eventorback.userstop.domain.dto.response.GetUserStopByIdentifierResponse;

public interface CustomUserStopRepository {

	List<GetUserStopByIdentifierResponse> getUserStopByIdentifier(String identifier);
}
