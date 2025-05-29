package com.eventorback.tour.domain.dto.response;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;

@Builder
public record GetTourResponse(
	// 공통 정보
	String contentId,
	String contentTypeId,
	String title,
	String overview,
	String tel,
	String homepage,
	String address,
	String zipcode,
	String mapX,
	String mapY,

	// 소개 정보
	String infoCenter,
	String openDate,
	String restDate,
	String useTime,
	String parking,
	String babyCarriage,
	String petAllowed,
	String creditCard,

	// 부가 정보
	String admission,
	String toilet,

	// 이미지 리스트
	List<String> images
) {
	public static GetTourResponse buildGetTourResponse(
		Map<String, Object> common,
		Map<String, Object> intro,
		Map<String, Object> info,
		Map<String, Object> image
	) {
		Map<String, Object> commonItem = extractFirstItem(common);
		Map<String, Object> introItem = extractFirstItem(intro);
		List<Map<String, Object>> infoItems = extractItemList(info);
		List<Map<String, Object>> imageItems = extractItemList(image);

		String admission = infoItems.stream()
			.filter(i -> "입 장 료".equals(i.get("infoname")))
			.map(i -> (String)i.get("infotext"))
			.findFirst().orElse(null);

		String toilet = infoItems.stream()
			.filter(i -> "화장실".equals(i.get("infoname")))
			.map(i -> (String)i.get("infotext"))
			.findFirst().orElse(null);

		List<String> imageList = imageItems.stream()
			.map(img -> (String)img.get("originimgurl"))
			.filter(Objects::nonNull)
			.toList();

		return new GetTourResponse(
			(String)commonItem.get("contentid"),
			(String)commonItem.get("contenttypeid"),
			(String)commonItem.get("title"),
			(String)commonItem.get("overview"),
			(String)commonItem.get("tel"),
			extractHomepage((String)commonItem.get("homepage")),
			combineAddress((String)commonItem.get("addr1"), (String)commonItem.get("addr2")),
			(String)commonItem.get("zipcode"),
			(String)commonItem.get("mapx"),
			(String)commonItem.get("mapy"),

			(String)introItem.get("infocenter"),
			(String)introItem.get("opendate"),
			(String)introItem.get("restdate"),
			(String)introItem.get("usetime"),
			(String)introItem.get("parking"),
			(String)introItem.get("chkbabycarriage"),
			(String)introItem.get("chkpet"),
			(String)introItem.get("chkcreditcard"),

			admission,
			toilet,

			imageList
		);
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Object> extractFirstItem(Map<String, Object> response) {
		if (response == null)
			return Map.of();

		Object responseObj = response.get("response");
		if (!(responseObj instanceof Map responseMap))
			return Map.of();

		Object bodyObj = responseMap.get("body");
		if (!(bodyObj instanceof Map bodyMap))
			return Map.of();

		Object itemsObj = bodyMap.get("items");
		if (!(itemsObj instanceof Map itemsMap))
			return Map.of();

		Object itemObj = itemsMap.get("item");
		if (itemObj instanceof List<?> itemList && !itemList.isEmpty()) {
			Object firstItem = itemList.get(0);
			if (firstItem instanceof Map<?, ?> map) {
				return (Map<String, Object>)map;
			}
		} else if (itemObj instanceof Map<?, ?> map) {
			return (Map<String, Object>)map;
		}

		return Map.of();
	}

	@SuppressWarnings("unchecked")
	private static List<Map<String, Object>> extractItemList(Map<String, Object> response) {
		if (response == null)
			return List.of();

		Object responseObj = response.get("response");
		if (!(responseObj instanceof Map responseMap))
			return List.of();

		Object bodyObj = responseMap.get("body");
		if (!(bodyObj instanceof Map bodyMap))
			return List.of();

		Object itemsObj = bodyMap.get("items");
		if (!(itemsObj instanceof Map itemsMap))
			return List.of();

		Object itemObj = itemsMap.get("item");

		if (itemObj instanceof List<?> itemList) {
			return itemList.stream()
				.filter(e -> e instanceof Map)
				.map(e -> (Map<String, Object>)e)
				.toList();
		} else if (itemObj instanceof Map<?, ?> singleItemMap) {
			return List.of((Map<String, Object>)singleItemMap);
		}

		return List.of();
	}

	private static String extractHomepage(String rawHtml) {
		if (rawHtml == null)
			return null;
		Matcher matcher = Pattern.compile("href=\\\"(.*?)\\\"").matcher(rawHtml);
		return matcher.find() ? matcher.group(1) : null;
	}

	private static String combineAddress(String addr1, String addr2) {
		if (addr2 == null || addr2.isBlank())
			return addr1;
		return addr1 + " " + addr2;
	}

}
