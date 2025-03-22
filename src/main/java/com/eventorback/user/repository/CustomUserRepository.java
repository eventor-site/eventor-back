package com.eventorback.user.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserByUserId;
import com.eventorback.user.domain.dto.response.GetUserListResponse;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.GetUserTokenInfo;
import com.eventorback.user.domain.dto.response.OauthDto;
import com.eventorback.user.domain.entity.User;

public interface CustomUserRepository {

	Page<GetUserListResponse> getUsers(Pageable pageable);

	List<GetUserByIdentifier> searchUserByIdentifier(String keyword);

	List<GetUserByUserId> searchUserByUserId(Long userId);

	Optional<User> getUser(Long userId);

	Optional<User> getUser(String identifier);

	GetUserTokenInfo getUserInfoByOauth(OauthDto request);

	Optional<GetUserResponse> getUserInfo(Long userId);

	List<Long> getDormantUsers();

	List<Long> getStopUsers();

	List<Long> getNotAdminUsers();

}
