package com.eventorback.tour.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.tour.client.GeocoderApiClient;
import com.eventorback.tour.client.TourApiClient;
import com.eventorback.tour.domain.dto.response.GeoPointResponse;
import com.eventorback.tour.domain.dto.response.GetTourResponse;
import com.eventorback.tour.domain.dto.response.SearchFestivalResponse;
import com.eventorback.tour.domain.dto.response.SearchTourResponse;
import com.eventorback.tour.domain.dto.response.TourApiResponse;
import com.eventorback.tour.exception.AddressBadRequestException;
import com.eventorback.tour.service.TourService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TourServiceImpl implements TourService {
	private final TourApiClient tourApiClient;
	private final GeocoderApiClient geocoderApiClient;

	@Value("${openApi.geocoder}")
	private String geocorderApiKey;

	@Value("${openApi.tour}")
	private String tourApiKey;

	@Override
	public SearchTourResponse searchTour(String address, String radius) {
		Map<String, Object> geocoderApiResponse = geocoderApiClient.convertAddress("address", "GetCoord", address,
			"road", geocorderApiKey);

		GeoPointResponse point = GeoPointResponse.fromGeocoderApiResponse(geocoderApiResponse);

		if (point == null) {
			throw new AddressBadRequestException();
		}

		Map<String, Object> tourApiResponse = tourApiClient.getLocationBasedList2(50, "WEB", "이벤터", "json", "S",
			point.x(), point.y(), radius, "12", tourApiKey);

		Map<String, Object> hotelResponse = tourApiClient.getLocationBasedList2(50, "WEB", "이벤터", "json", "S",
			point.x(), point.y(), radius, "39", tourApiKey);

		Map<String, Object> eateryResponse = tourApiClient.getLocationBasedList2(50, "WEB", "이벤터", "json", "S",
			point.x(), point.y(), radius, "32", tourApiKey);

		List<TourApiResponse> tours = TourApiResponse.parseTourApiResponse(tourApiResponse);
		List<TourApiResponse> hotels = TourApiResponse.parseTourApiResponse(hotelResponse);
		List<TourApiResponse> eateries = TourApiResponse.parseTourApiResponse(eateryResponse);

		return new SearchTourResponse(tours, hotels, eateries);
	}

	@Override
	public GetTourResponse getTour(String contentId, String contentTypeId) {

		Map<String, Object> detailCommon2 = tourApiClient.getDetailCommon2("WEB", "이벤터", "json", contentId,
			tourApiKey);

		Map<String, Object> detailIntro2 = tourApiClient.getDetailIntro2("WEB", "이벤터", "json", contentId,
			contentTypeId, tourApiKey);

		Map<String, Object> detailInfo2 = tourApiClient.getDetailInfo2("WEB", "이벤터", "json", contentId,
			contentTypeId, tourApiKey);

		Map<String, Object> detailImage2 = tourApiClient.getDetailImage2("WEB", "이벤터", "json", contentId,
			tourApiKey);

		return GetTourResponse.buildGetTourResponse(detailCommon2, detailIntro2, detailInfo2, detailImage2);

	}

	@Override
	public List<SearchFestivalResponse> searchFestival2() {
		LocalDate today = LocalDate.now();
		String eventStartDate;
		String eventEndDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

		if (today.getMonthValue() == 1) {
			// 1월인 경우 전년도 12월 1일을 시작일로
			LocalDate start = LocalDate.of(today.getYear() - 1, 12, 1);
			eventStartDate = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		} else {
			// 그 외는 올해 1월 1일
			LocalDate start = LocalDate.of(today.getYear(), 1, 1);
			eventStartDate = start.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		}

		Map<String, Object> searchFestival2 = tourApiClient.searchFestival2(100, "WEB", "이벤터", "json", "R",
			eventStartDate,
			eventEndDate, tourApiKey);

		ObjectMapper objectMapper = new ObjectMapper();
		List<SearchFestivalResponse> result = new ArrayList<>();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		try {
			// Map → JsonNode 변환
			JsonNode root = objectMapper.convertValue(searchFestival2, JsonNode.class);
			JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

			if (itemsNode.isArray()) {
				for (JsonNode item : itemsNode) {
					result.add(
						SearchFestivalResponse.builder()
							.contentId(item.path("contentid").asText(null))
							.contentTypeId(item.path("contenttypeid").asText(null))
							.title(item.path("title").asText(null))
							.addr1(item.path("addr1").asText(null))
							.addr2(item.path("addr2").asText(null))
							.eventStartDate(item.path("eventstartdate").asText(null))
							.eventEndDate(item.path("eventenddate").asText(null))
							.firstImage(item.path("firstimage").asText(null))
							.tel(item.path("tel").asText(null))
							.build()
					);
				}
			}

			// 시작일 기준 최신순으로 정렬
			result.sort((a, b) -> {
				try {
					LocalDate dateA = LocalDate.parse(a.eventStartDate(), formatter);
					LocalDate dateB = LocalDate.parse(b.eventStartDate(), formatter);
					return dateB.compareTo(dateA); // 최신 날짜가 먼저
				} catch (Exception e) {
					return 0; // 파싱 실패 시 정렬 무시
				}
			});

		} catch (Exception ignored) {
		}

		return result;
	}

}
