package com.eventorback.image.domain.dto.response;

import com.eventorback.image.domain.entity.Image;

import lombok.Builder;

@Builder
public record GetImageResponse(
	String originalName,
	String url
) {
	public static GetImageResponse fromEntity(Image image) {
		return GetImageResponse.builder()
			.originalName(image.getOriginalName())
			.url(image.getUrl())
			.build();
	}
}
