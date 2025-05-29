package com.eventorback.tour.domain.dto.response;

import java.util.Map;

import lombok.Builder;

@Builder
public record GeoPointResponse(
	String x,
	String y) {

	public static GeoPointResponse fromGeocoderApiResponse(Map<String, Object> geocoderApiResponse) {
		if (geocoderApiResponse == null)
			return null;

		try {
			Map<String, Object> responseMap = (Map<String, Object>)geocoderApiResponse.get("response");
			Map<String, Object> resultMap = (Map<String, Object>)responseMap.get("result");
			Map<String, String> pointMap = (Map<String, String>)resultMap.get("point");

			String x = pointMap.get("x");
			String y = pointMap.get("y");

			return GeoPointResponse.builder()
				.x(x)
				.y(y)
				.build();
		} catch (ClassCastException | NullPointerException e) {
			// 필요시 로그 추가
			return null;
		}
	}
}
