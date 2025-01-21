package com.eventorback.user.repository;

import java.util.List;
import java.util.Optional;

import com.eventorback.user.domain.dto.response.GetUserByIdentifier;
import com.eventorback.user.domain.dto.response.GetUserResponse;
import com.eventorback.user.domain.dto.response.Oauth2Dto;
import com.eventorback.user.domain.entity.User;

public interface CustomUserRepository {

	List<GetUserByIdentifier> searchUserByIdentifier(String keyword);

	Optional<User> getUser(Long userId);

	Optional<User> getUser(String identifier);

	Optional<Oauth2Dto> getOauth2ByIdentifier(String identifier);

	Optional<GetUserResponse> getUserInfo(Long userId);

}
