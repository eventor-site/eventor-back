package com.eventorback.tour.domain.dto.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Builder;

@Builder
public record TourApiResponse(
	String addr1,
	String contentid,
	String contentTypeId,
	String title,
	String dist,
	String tel,
	String firstimage
) {
	public static List<TourApiResponse> parseTourApiResponse(Map<String, Object> tourApiResponse) {
		if (tourApiResponse == null)
			return List.of();

		// response → body → items → item
		Map<String, Object> response = (Map<String, Object>)tourApiResponse.get("response");
		if (response == null)
			return List.of();

		Map<String, Object> body = (Map<String, Object>)response.get("body");
		if (body == null)
			return List.of();

		Object itemsObj = body.get("items");

		if (!(itemsObj instanceof Map)) {
			return List.of(); // 또는 에러 로깅 후 빈 리스트 반환
		}

		Map<String, Object> items = (Map<String, Object>)itemsObj;

		Object rawItem = items.get("item");

		// item이 리스트인지 단일 객체인지 확인
		List<Map<String, String>> itemList = new ArrayList<>();
		if (rawItem instanceof List) {
			itemList = (List<Map<String, String>>)rawItem;
		} else if (rawItem instanceof Map) {
			itemList.add((Map<String, String>)rawItem);
		}

		return itemList.stream().map(TourApiResponse::fromItem).toList();
	}

	public static TourApiResponse fromItem(Map<String, String> item) {
		return TourApiResponse.builder()
			.addr1(item.getOrDefault("addr1", ""))
			.contentid(item.getOrDefault("contentid", ""))
			.contentTypeId(item.getOrDefault("contenttypeid", ""))
			.title(item.getOrDefault("title", ""))
			.dist(convertDistance(item.getOrDefault("dist", "")))
			.tel(item.getOrDefault("tel", ""))
			.firstimage(item.getOrDefault("firstimage", ""))
			.build();
	}

	public static String convertDistance(String distance) {
		try {
			double meters = Double.parseDouble(distance);
			if (meters >= 1000) {
				double kilometers = meters / 1000;
				return String.format("%.1fkm", kilometers);
			} else {
				return String.format("%.0fm", meters);
			}
		} catch (NumberFormatException e) {
			// 잘못된 숫자 입력 처리
			return "N/A";
		}
	}
}
