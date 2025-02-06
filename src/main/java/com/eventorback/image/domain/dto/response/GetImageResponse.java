package com.eventorback.image.domain.dto.response;

import com.eventorback.image.domain.entity.Image;

import lombok.Builder;

@Builder
public record GetImageResponse(
	Long imageId,
	String originalName,
	String url,
	Long size
) {
	public static GetImageResponse fromEntity(Image image) {
		return GetImageResponse.builder()
			.imageId(image.getImageId())
			.originalName(image.getOriginalName())
			.url(image.getUrl())
			.size(image.getSize())
			.build();
	}
}
