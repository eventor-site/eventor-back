package com.eventorback.user.service;

import java.util.List;

import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.OauthDto;
import com.eventorback.user.domain.dto.response.UserTokenInfo;

public interface UserService {

	List<GetUserByIdentifier> searchUserByIdentifier(String keyword);

	UserTokenInfo getUserTokenInfoByIdentifier(String identifier);

	UserTokenInfo getUserInfoByOauth(OauthDto request);

	Boolean existsByOauth(OauthDto request);

	GetUserResponse getUserInfo(Long userId);

	void updateUser(Long userId, UpdateUserRequest request);

	void withdrawUser(Long userId);

	Boolean meCheckRoles(CurrentUserDto currentUser);

	String meCheckNickname(Long userId, CheckNicknameRequest request);

	void updateLastLoginTime(UpdateLastLoginTimeRequest lastLoginTime);

	String modifyPassword(Long userId, ModifyPasswordRequest request);

	void signup(SignUpRequest request);

	String checkIdentifier(CheckIdentifierRequest request);

	String checkNickname(CheckNicknameRequest request);

	boolean existsByEmail(String email);

	String recoverIdentifier(String email);

	String recoverPassword(String identifier);

}
