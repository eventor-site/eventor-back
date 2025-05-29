package com.eventorback.tour.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "geocoder-api-client", url = "https://api.vworld.kr/req")
public interface GeocoderApiClient {

	@GetMapping("/address")
	Map<String, Object> convertAddress(@RequestParam String service, @RequestParam String request,
		@RequestParam String address, @RequestParam String type, @RequestParam String key);

}