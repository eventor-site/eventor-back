package com.eventorback.oauth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventorback.oauth.dto.OauthTokenResponse;

@FeignClient(name = "googleTokenClient", url = "https://oauth2.googleapis.com/token")
public interface GoogleTokenClient {

	@PostMapping
	OauthTokenResponse getToken(
		@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("code") String code,
		@RequestParam("redirect_uri") String redirectUri
	);
}