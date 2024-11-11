package com.eventorback.user.repository;

import java.util.List;

import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;

public interface CustomUserRepository {

	List<GetUserByAddShopResponse> searchUserById(String keyword);
}
