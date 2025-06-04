package com.eventorback.tour.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "tour-api-client", url = "https://apis.data.go.kr/B551011/KorService2")
public interface TourApiClient {

	@GetMapping("/locationBasedList2")
	Map<String, Object> getLocationBasedList2(@RequestParam Integer numOfRows, @RequestParam String MobileOS,
		@RequestParam String MobileApp,
		@RequestParam String _type, @RequestParam String arrange,
		@RequestParam String mapX, @RequestParam String mapY, @RequestParam String radius,
		@RequestParam String contentTypeId, @RequestParam String serviceKey);

	@GetMapping("/detailCommon2")
	Map<String, Object> getDetailCommon2(@RequestParam String MobileOS, @RequestParam String MobileApp,
		@RequestParam String _type, @RequestParam String contentId, @RequestParam String serviceKey);

	@GetMapping("/detailIntro2")
	Map<String, Object> getDetailIntro2(@RequestParam String MobileOS, @RequestParam String MobileApp,
		@RequestParam String _type, @RequestParam String contentId, @RequestParam String contentTypeId,
		@RequestParam String serviceKey);

	@GetMapping("/detailInfo2")
	Map<String, Object> getDetailInfo2(@RequestParam String MobileOS, @RequestParam String MobileApp,
		@RequestParam String _type, @RequestParam String contentId, @RequestParam String contentTypeId,
		@RequestParam String serviceKey);

	@GetMapping("/detailImage2")
	Map<String, Object> getDetailImage2(@RequestParam String MobileOS, @RequestParam String MobileApp,
		@RequestParam String _type, @RequestParam String contentId, @RequestParam String serviceKey);

	@GetMapping("/searchFestival2")
	Map<String, Object> searchFestival2(@RequestParam Integer numOfRows, @RequestParam String MobileOS,
		@RequestParam String MobileApp, @RequestParam String _type, @RequestParam String arrange,
		@RequestParam String eventStartDate, @RequestParam String eventEndDate, @RequestParam String serviceKey);

}
