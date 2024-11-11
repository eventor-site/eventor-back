package com.eventorback.user.service;

import java.util.List;

import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.response.GetUserByAddShopResponse;
import com.eventorback.user.domain.dto.response.UserTokenInfo;

public interface UserService {

	void signUp(SignUpRequest request);

	UserTokenInfo getUserTokenInfoById(String id);

	List<GetUserByAddShopResponse> searchUserById(String keyword);

}
