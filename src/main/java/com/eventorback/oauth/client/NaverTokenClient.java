package com.eventorback.oauth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.eventorback.oauth.dto.OauthTokenResponse;

@FeignClient(name = "naverTokenClient", url = "https://nid.naver.com")
public interface NaverTokenClient {

	@PostMapping("/oauth2.0/token")
	OauthTokenResponse getToken(
		@RequestParam("grant_type") String grantType,
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("code") String code,
		@RequestParam("state") String state
	);
}