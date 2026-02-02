package com.eventorback.oauth.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "naverProfileClient", url = "https://openapi.naver.com/v1/nid/me")
public interface NaverProfileClient {

	@GetMapping
	Map<String, Object> getUserProfile(@RequestHeader("Authorization") String authorizationHeader);
}
