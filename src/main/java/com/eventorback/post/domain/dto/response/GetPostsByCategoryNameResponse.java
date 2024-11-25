package com.eventorback.post.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;

@Builder
public record GetPostsByCategoryNameResponse(
	String status,
	String message,
	LocalDateTime serverTime,
	Boolean isAuthorized,
	List<GetPostSimpleResponse> data
) {
	public static GetPostsByCategoryNameResponse fromResponse(List<GetPostSimpleResponse> posts, Boolean isAuthorized) {
		return GetPostsByCategoryNameResponse.builder()
			.status("Success")
			.message("성공")
			.serverTime(LocalDateTime.now())
			.isAuthorized(isAuthorized)
			.data(posts)
			.build();
	}
}
