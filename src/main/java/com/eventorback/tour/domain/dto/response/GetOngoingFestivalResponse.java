package com.eventorback.tour.domain.dto.response;

import lombok.Builder;

@Builder
public record GetOngoingFestivalResponse(
	String contentId,         // 내용 ID
	String contentTypeId,     // 내용 타입 ID
	String title,            // 축제 제목
	String firstImage
) {
	public static GetOngoingFestivalResponse fromDTO(SearchFestivalResponse response) {
		return GetOngoingFestivalResponse.builder()
			.contentId(response.contentId())
			.contentTypeId(response.contentTypeId())
			.title(response.title())
			.firstImage(response.firstImage())
			.build();
	}
}