package com.eventorback.user.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.dto.request.CheckIdentifierRequest;
import com.eventorback.user.domain.dto.request.CheckNicknameRequest;
import com.eventorback.user.domain.dto.request.ModifyPasswordRequest;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;
import com.eventorback.user.domain.dto.request.UpdateUserAttributeRequest;
import com.eventorback.user.domain.dto.request.UpdateUserRequest;
import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;

public interface UserService {

	Page<GetUserListResponse> getUsers(Pageable pageable);

	List<GetUserByIdentifier> searchUserByIdentifier(String keyword);

	List<GetUserByUserId> searchUserByUserId(Long userId);



	GetUserResponse getUserInfo(Long userId);

	void updateUser(Long userId, UpdateUserRequest request);

	void updateUserByAdmin(Long userId, UpdateUserRequest request);

	void updateUserAttributeByAdmin(Long userId, UpdateUserAttributeRequest request);

	void withdrawUser(Long userId);

	Boolean meCheckRoles(CurrentUserDto currentUser, String roleName);

	List<String> meRoles(CurrentUserDto currentUser);

	String meCheckNickname(Long userId, CheckNicknameRequest request);

	void updateLoginAt(UpdateLoginAtRequest loginAt);

	String modifyPassword(Long userId, ModifyPasswordRequest request);

	void signup(SignUpRequest request);

	/** 아이디·비밀번호 가입: 형식·중복·금지 닉네임·이메일 인증을 서버에서 확인한다 */
	void signupWithIdentifier(SignUpRequest request);

	String checkIdentifier(CheckIdentifierRequest request);

	String checkNickname(CheckNicknameRequest request);





	void softDeleteExpiredUsers();

	void updateDormantUsers();

	void unlockExpiredStopUsers();

}
