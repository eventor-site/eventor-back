package com.eventorback.userstop.repository;

import java.util.List;

import com.eventorback.userstop.domain.dto.response.GetUserStopByUserIdResponse;

public interface CustomUserStopRepository {

	List<GetUserStopByUserIdResponse> getUserStopByUserId(Long userId);
}
