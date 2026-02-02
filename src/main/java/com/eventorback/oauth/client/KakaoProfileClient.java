package com.eventorback.oauth.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kakaoProfileClient", url = "https://kapi.kakao.com/v2/user/me")
public interface KakaoProfileClient {

	@GetMapping
	Map<String, Object> getUserProfile(@RequestHeader("Authorization") String authorizationHeader,
		@RequestParam("target_id_type") String targetIdType,
		@RequestParam("target_id") Long targetId);
}
