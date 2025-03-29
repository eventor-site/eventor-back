package com.eventorback.search.document.dto.reponse;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Builder;

@Builder
public record SearchPostsResponse(
	Long postId,
	String categoryName,
	String statusName,
	String writer,
	String writerGrade,
	String title,
	String content,
	Long recommendationCount,
	Long viewCount,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
	LocalDateTime createdAt,

	String productName,
	String shoppingMall,

	String eventStatusName,
	Integer remainingDay,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
	LocalDateTime startTime,
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
	LocalDateTime endTime,
	String endType,

	String imageUrl,
	String imageType
) {
	public static SearchPostsResponse addInfo(SearchPostsResponse response) {
		LocalDateTime now = LocalDateTime.now();

		// eventStatusName 계산
		String eventStatusName;
		if (response.startTime() != null && response.startTime().isAfter(now)) {
			eventStatusName = "예정";
		} else if (response.endTime() == null ||
			(response.startTime() != null && response.startTime().isBefore(now) && response.endTime()
				.isAfter(now))) {
			eventStatusName = "진행중";
		} else if (response.endTime().isBefore(now)) {
			eventStatusName = "마감";
		} else {
			eventStatusName = "미정";
		}

		// remainingDay 계산
		Integer remainingDay = null;
		if (response.startTime() != null) {
			remainingDay = (int)ChronoUnit.DAYS.between(now, response.startTime());
		}

		return SearchPostsResponse.builder()
			.postId(response.postId())
			.categoryName(response.categoryName())
			.statusName(response.statusName())
			.writer(response.writer())
			.writerGrade(response.writerGrade())
			.title(response.title())
			.content(response.content())
			.recommendationCount(response.recommendationCount())
			.viewCount(response.viewCount())
			.createdAt(response.createdAt())
			.productName(response.productName())
			.shoppingMall(response.shoppingMall())
			.eventStatusName(eventStatusName)
			.remainingDay(remainingDay)
			.startTime(response.startTime())
			.endTime(response.endTime())
			.endType(response.endType())
			.imageUrl(response.imageUrl())
			.imageType(response.imageType())
			.build();
	}
}
