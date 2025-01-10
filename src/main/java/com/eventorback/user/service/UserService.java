package com.eventorback.user.service;

import java.util.List;

import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.UserTokenInfo;

public interface UserService {

	List<GetUserByAddShopResponse> searchUserById(String keyword);

	UserTokenInfo getUserTokenInfoByIdentifier(String identifier);

	GetUserResponse getUserInfo(Long userId);

	void signup(SignUpRequest request);

	String checkIdentifier(CheckIdentifierRequest request);

	void updateUser(Long userId, UpdateUserRequest request);

	void updateLastLoginTime(UpdateLastLoginTimeRequest lastLoginTime);

	String modifyPassword(Long userId, ModifyPasswordRequest request);

	void withdrawUser(Long userId);

	boolean existsByEmail(String email);

	String recoverIdentifier(String email);

	String recoverPassword(String identifier);

}
