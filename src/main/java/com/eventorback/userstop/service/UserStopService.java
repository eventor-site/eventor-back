package com.eventorback.userstop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.userstop.domain.dto.UserStopDto;
import com.eventorback.userstop.domain.dto.response.GetUserStopResponse;

public interface UserStopService {

	List<GetUserStopResponse> getUserStops();

	Page<UserStopDto> getUserStops(Pageable pageable);

	UserStopDto getUserStop(Long getUserStopId);

	void createUserStop(UserStopDto request);

	void deleteUserStop(Long userStopId);
}
