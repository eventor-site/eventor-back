package com.eventorback.tour.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.global.dto.ApiResponse;
import com.eventorback.tour.domain.dto.response.GetTourResponse;
import com.eventorback.tour.domain.dto.response.SearchTourResponse;
import com.eventorback.tour.service.TourService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/tours")
public class TourController {
	private final TourService tourApiService;

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<SearchTourResponse>> searchTour(
		@RequestParam String address, @RequestParam String radius) {
		return ApiResponse.createSuccess(tourApiService.searchTour(address, radius));
	}

	@GetMapping("/{contentId}")
	public ResponseEntity<ApiResponse<GetTourResponse>> getTour(
		@PathVariable String contentId, @RequestParam String contentTypeId) {
		return ApiResponse.createSuccess(tourApiService.getTour(contentId, contentTypeId));
	}

}
