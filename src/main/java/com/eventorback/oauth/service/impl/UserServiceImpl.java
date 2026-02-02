// package com.eventorback.oauth.service.impl;
//
// import org.springframework.stereotype.Service;
//
// import com.eventorback.auth.client.UserClient;
// import com.eventorback.auth.dto.request.CheckNicknameRequest;
// import com.eventorback.global.dto.ApiResponse;
// import com.eventorback.oauth.service.UserService;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class UserServiceImpl implements UserService {
// 	private final UserClient userClient;
//
// 	public ApiResponse<Void> checkNickname(CheckNicknameRequest request) {
// 		return userClient.checkNickname(request).getBody();
// 	}
// }
