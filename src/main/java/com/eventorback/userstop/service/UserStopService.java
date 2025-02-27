package com.eventorback.userstop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.userstop.domain.dto.UserStopDto;
import com.eventorback.userstop.domain.dto.response.GetUserStopByUserIdResponse;
import com.eventorback.userstop.domain.dto.response.GetUserStopResponse;

public interface UserStopService {

	Page<GetUserStopResponse> getUserStops(Pageable pageable);

	UserStopDto getUserStop(Long getUserStopId);

	List<GetUserStopByUserIdResponse> getUserStopsByUserId(Long userId);

	void createUserStop(UserStopDto request);

	void deleteUserStop(Long userStopId);
}
