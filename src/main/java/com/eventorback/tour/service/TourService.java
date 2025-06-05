package com.eventorback.tour.service;

import java.util.List;

import com.eventorback.tour.domain.dto.response.GetOngoingFestivalResponse;
import com.eventorback.tour.domain.dto.response.GetTourResponse;
import com.eventorback.tour.domain.dto.response.SearchFestivalResponse;
import com.eventorback.tour.domain.dto.response.SearchTourResponse;

public interface TourService {

	SearchTourResponse searchTour(String address, String radius);

	GetTourResponse getTour(String contentId, String contentTypeId);

	List<SearchFestivalResponse> searchFestival2();

	List<GetOngoingFestivalResponse> getOngoingFestivals();

	void evictSearchFestival2Cache();

}
