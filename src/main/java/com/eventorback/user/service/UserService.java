package com.eventorback.user.service;

import java.util.List;

import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.UserTokenInfo;

public interface UserService {

	void signUp(SignUpRequest request);

	UserTokenInfo getUserTokenInfoByIdentifier(String identifier);

	List<GetUserByAddShopResponse> searchUserById(String keyword);

	GetUserResponse getUserInfo(Long userId);

	void updateUser(Long userId, UpdateUserRequest request);

	void updateLastLoginTime(Long userId, UpdateLastLoginTimeRequest lastLoginTime);

}
