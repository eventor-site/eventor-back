package com.eventorback.oauth.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "googleProfileClient", url = "https://www.googleapis.com/oauth2/v3/userinfo")
public interface GoogleProfileClient {

	@GetMapping
	Map<String, Object> getUserProfile(@RequestHeader("Authorization") String authorizationHeader);
}
