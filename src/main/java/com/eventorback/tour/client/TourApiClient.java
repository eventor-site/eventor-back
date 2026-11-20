package com.eventorback.tour.client;

import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 한국관광공사 국문 관광정보 서비스(KorService2). 이전 Feign 인터페이스와 같은 메서드를 RestClient로 구현했다.
 */
@Component
public class TourApiClient {
	private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService2";
	private static final ParameterizedTypeReference<Map<String, Object>> MAP = new ParameterizedTypeReference<>() {
	};

	private final RestClient restClient;

	public TourApiClient(RestClient.Builder builder) {
		this.restClient = builder.build();
	}

	public Map<String, Object> getDetailCommon2(String MobileOS, String MobileApp, String _type, String contentId,
		String serviceKey) {
		return get(common("/detailCommon2", MobileOS, MobileApp, _type, contentId, serviceKey));
	}

	public Map<String, Object> getDetailIntro2(String MobileOS, String MobileApp, String _type, String contentId,
		String contentTypeId, String serviceKey) {
		return get(common("/detailIntro2", MobileOS, MobileApp, _type, contentId, serviceKey)
			.queryParam("contentTypeId", contentTypeId));
	}

	public Map<String, Object> getDetailInfo2(String MobileOS, String MobileApp, String _type, String contentId,
		String contentTypeId, String serviceKey) {
		return get(common("/detailInfo2", MobileOS, MobileApp, _type, contentId, serviceKey)
			.queryParam("contentTypeId", contentTypeId));
	}

	public Map<String, Object> getDetailImage2(String MobileOS, String MobileApp, String _type, String contentId,
		String serviceKey) {
		return get(common("/detailImage2", MobileOS, MobileApp, _type, contentId, serviceKey));
	}

	public Map<String, Object> searchFestival2(Integer numOfRows, String MobileOS, String MobileApp, String _type,
		String arrange, String eventStartDate, String eventEndDate, String serviceKey) {
		return get(UriComponentsBuilder.fromUriString(BASE_URL + "/searchFestival2")
			.queryParam("numOfRows", numOfRows).queryParam("MobileOS", MobileOS).queryParam("MobileApp", MobileApp)
			.queryParam("_type", _type).queryParam("arrange", arrange).queryParam("eventStartDate", eventStartDate)
			.queryParam("eventEndDate", eventEndDate).queryParam("serviceKey", serviceKey));
	}

	private static UriComponentsBuilder common(String path, String MobileOS, String MobileApp, String _type,
		String contentId, String serviceKey) {
		return UriComponentsBuilder.fromUriString(BASE_URL + path)
			.queryParam("MobileOS", MobileOS).queryParam("MobileApp", MobileApp).queryParam("_type", _type)
			.queryParam("contentId", contentId).queryParam("serviceKey", serviceKey);
	}

	private Map<String, Object> get(UriComponentsBuilder uri) {
		return restClient.get().uri(StrictQuery.toUri(uri)).retrieve().body(MAP);
	}
}
