package com.eventorback.userstop.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.userstop.domain.dto.response.GetUserStopByUserIdResponse;
import com.eventorback.userstop.domain.dto.response.GetUserStopResponse;

public interface UserStopCustomRepository {

	Page<GetUserStopResponse> getUserStops(Pageable pageable);

	List<GetUserStopByUserIdResponse> getUserStopByUserId(Long userId);
}
