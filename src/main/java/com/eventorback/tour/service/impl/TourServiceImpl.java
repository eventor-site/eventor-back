package com.eventorback.tour.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.tour.client.GeocoderApiClient;
import com.eventorback.tour.client.TourApiClient;
import com.eventorback.tour.domain.dto.response.GeoPointResponse;
import com.eventorback.tour.domain.dto.response.GetOngoingFestivalResponse;
import com.eventorback.tour.domain.dto.response.GetTourResponse;
import com.eventorback.tour.domain.dto.response.SearchFestivalResponse;
import com.eventorback.tour.domain.dto.response.SearchTourResponse;
import com.eventorback.tour.domain.dto.response.TourApiResponse;
import com.eventorback.tour.exception.AddressBadRequestException;
import com.eventorback.tour.exception.GecoderAPIServerException;
import com.eventorback.tour.exception.TourAPIServerException;
import com.eventorback.tour.service.TourService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		Map<String, Object> geocoderApiResponse;

		try {
			geocoderApiResponse = geocoderApiClient.convertAddress("address", "GetCoord", address,
				"road", geocorderApiKey);
		} catch (Exception e) {
			throw new GecoderAPIServerException();
		}

		GeoPointResponse point = GeoPointResponse.fromGeocoderApiResponse(geocoderApiResponse);

		if (point == null) {
			throw new AddressBadRequestException();
		}

		try {
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
		} catch (Exception e) {
			throw new TourAPIServerException();
		}

	}

	@Override
	public GetTourResponse getTour(String contentId, String contentTypeId) {

		try {
			Map<String, Object> detailCommon2 = tourApiClient.getDetailCommon2("WEB", "이벤터", "json", contentId,
				tourApiKey);

			Map<String, Object> detailIntro2 = tourApiClient.getDetailIntro2("WEB", "이벤터", "json", contentId,
				contentTypeId, tourApiKey);

			Map<String, Object> detailInfo2 = tourApiClient.getDetailInfo2("WEB", "이벤터", "json", contentId,
				contentTypeId, tourApiKey);

			Map<String, Object> detailImage2 = tourApiClient.getDetailImage2("WEB", "이벤터", "json", contentId,
				tourApiKey);

			return GetTourResponse.buildGetTourResponse(detailCommon2, detailIntro2, detailInfo2, detailImage2);
		} catch (Exception e) {
			throw new TourAPIServerException();
		}

	}

	@Override
	@Cacheable(cacheNames = "cache", key = "'searchFestival2'", cacheManager = "cacheManager")
	public List<SearchFestivalResponse> searchFestival2() {
		LocalDate today = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

		LocalDate start = today.minusDays(1);
		LocalDate end = today.plusMonths(3);

		String eventStartDate = start.format(formatter);
		String eventEndDate = end.format(formatter);

		ObjectMapper objectMapper = new ObjectMapper();
		List<SearchFestivalResponse> result = new ArrayList<>();

		try {
			Map<String, Object> searchFestival2 = tourApiClient.searchFestival2(1000, "WEB", "이벤터", "json", "R",
				eventStartDate, eventEndDate, tourApiKey);

			// Map → JsonNode 변환
			JsonNode root = objectMapper.convertValue(searchFestival2, JsonNode.class);
			JsonNode itemsNode = root.path("response").path("body").path("items").path("item");

			if (itemsNode.isArray()) {
				for (JsonNode item : itemsNode) {
					String startStr = item.path("eventstartdate").asText(null);
					String endStr = item.path("eventenddate").asText(null);

					LocalDate startDate = SearchFestivalResponse.parseDate(startStr, formatter);
					LocalDate endDate = SearchFestivalResponse.parseDate(endStr, formatter);

					String formattedStart = SearchFestivalResponse.formatWithDayOfWeek(startDate);
					String formattedEnd = SearchFestivalResponse.formatWithDayOfWeek(endDate);

					String status = SearchFestivalResponse.calculateEventStatus(startDate, endDate, today);
					Integer remaining = (endDate != null) ? (int)ChronoUnit.DAYS.between(today, startDate) : null;

					result.add(
						SearchFestivalResponse.builder()
							.contentId(item.path("contentid").asText(null))
							.contentTypeId(item.path("contenttypeid").asText(null))
							.title(item.path("title").asText(null))
							.addr1(item.path("addr1").asText(null))
							.addr2(item.path("addr2").asText(null))
							.eventStartDate(formattedStart)
							.eventEndDate(formattedEnd)
							.firstImage(item.path("firstimage").asText(null))
							.tel(item.path("tel").asText(null))
							.eventStatusName(status)
							.remainingDay(remaining)
							.rawEventStartDate(startDate)
							.build()
					);
				}
			}

			// 시작일 기준 최신순으로 정렬
			result.sort((a, b) -> {
				int priorityA = SearchFestivalResponse.getStatusPriority(a.eventStatusName());
				int priorityB = SearchFestivalResponse.getStatusPriority(b.eventStatusName());

				if (priorityA != priorityB) {
					return Integer.compare(priorityA, priorityB); // 우선순위 오름차순 (1이 먼저)
				}

				// 상태가 같으면 시작일 기준 최신순
				LocalDate dateA = a.rawEventStartDate();
				LocalDate dateB = b.rawEventStartDate();

				if (dateA == null || dateB == null)
					return 0;

				// 상태별 정렬 방식 분기
				return switch (a.eventStatusName()) {
					case "예정" -> dateA.compareTo(dateB);      // 가까운 순 (오름차순)
					default -> dateB.compareTo(dateA);          // 최신 순 (내림차순)
				};
			});

		} catch (Exception ignored) {
			return List.of();
		}

		return result;
	}

	@Override
	@Cacheable(cacheNames = "cache", key = "'getOngoingFestivals'", cacheManager = "cacheManager")
	public List<GetOngoingFestivalResponse> getOngoingFestivals() {
		return searchFestival2().stream().limit(10).map(GetOngoingFestivalResponse::fromDTO).toList();
	}

	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames = "cache", key = "'searchFestival2'", cacheManager = "cacheManager"),
		@CacheEvict(cacheNames = "cache", key = "'getOngoingFestivals'", cacheManager = "cacheManager")
	})
	public void evictSearchFestival2Cache() {
	}

}
