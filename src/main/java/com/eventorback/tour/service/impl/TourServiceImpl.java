package com.eventorback.tour.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.tour.client.GeocoderApiClient;
import com.eventorback.tour.client.TourApiClient;
import com.eventorback.tour.domain.dto.response.GeoPointResponse;
import com.eventorback.tour.domain.dto.response.GetTourResponse;
import com.eventorback.tour.domain.dto.response.SearchTourResponse;
import com.eventorback.tour.domain.dto.response.TourApiResponse;
import com.eventorback.tour.exception.AddressBadRequestException;
import com.eventorback.tour.service.TourService;

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

}
