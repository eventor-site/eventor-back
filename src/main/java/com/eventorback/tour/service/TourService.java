package com.eventorback.tour.service;

import com.eventorback.tour.domain.dto.response.GetTourResponse;
import com.eventorback.tour.domain.dto.response.SearchTourResponse;

public interface TourService {

	SearchTourResponse searchTour(String address, String radius);

	GetTourResponse getTour(String contentId, String contentTypeId);

}
