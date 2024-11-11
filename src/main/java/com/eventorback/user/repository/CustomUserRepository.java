package com.eventorback.user.repository;

import java.util.List;

import com.sikyeojoback.user.domain.dto.response.GetUserByAddShopResponse;

public interface CustomUserRepository {

	List<GetUserByAddShopResponse> searchUserById(String keyword);
}
