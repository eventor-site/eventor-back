package com.eventorback.oauth.service;

import java.util.Map;

import com.eventorback.oauth.dto.OauthTokenResponse;
import com.eventorback.user.domain.dto.request.SignUpRequest;
import com.eventorback.user.domain.dto.response.OauthDto;

import jakarta.servlet.http.HttpServletResponse;

public interface OauthService {

	String authentication(String registrationId);

	SignUpRequest getToken(String registrationId, String code);

	Map<String, Object> getUserAttributes(String registrationId, OauthTokenResponse tokenResponse);

	Boolean existsByOauth(OauthDto request);

	void oauthSignup(SignUpRequest request);

	void oauthLogin(OauthDto request, HttpServletResponse response);

	String createRedirectUrl(String accessToken, String refreshToken, String oauthId, String error);

}