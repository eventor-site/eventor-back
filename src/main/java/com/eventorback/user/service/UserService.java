package com.eventorback.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.RecoverOauthRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLastLoginTimeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserAttributeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserAuth;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserOauth;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.OauthDto;

public interface UserService {

	Page<GetUserListResponse> getUsers(Pageable pageable);

	List<GetUserByIdentifier> searchUserByIdentifier(String keyword);

	List<GetUserByUserId> searchUserByUserId(Long userId);

	GetUserAuth getAuthByIdentifier(String identifier);

	GetUserOauth getOAuthInfoByOauth(OauthDto request);

	Boolean existsByOauth(OauthDto request);

	GetUserResponse getUserInfo(Long userId);

	void updateUser(Long userId, UpdateUserRequest request);

	void updateUserByAdmin(Long userId, UpdateUserRequest request);

	void updateUserAttributeByAdmin(Long userId, UpdateUserAttributeRequest request);

	void withdrawUser(Long userId);

	Boolean meCheckRoles(CurrentUserDto currentUser, String roleName);

	List<String> meRoles(CurrentUserDto currentUser);

	String meCheckNickname(Long userId, CheckNicknameRequest request);

	void updateLastLoginTime(UpdateLastLoginTimeRequest lastLoginTime);

	String modifyPassword(Long userId, ModifyPasswordRequest request);

	void signup(SignUpRequest request);

	String checkIdentifier(CheckIdentifierRequest request);

	String checkNickname(CheckNicknameRequest request);

	String recoverIdentifier(String identifier);

	String recoverPassword(String identifier);

	String recover(String identifier);

	String recoverOauth(RecoverOauthRequest request);

}
