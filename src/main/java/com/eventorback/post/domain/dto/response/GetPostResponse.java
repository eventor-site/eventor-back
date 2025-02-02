package com.eventorback.post.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.post.domain.entity.Post;

import lombok.Builder;

@Builder
public record GetPostResponse(
	Long postId,
	String categoryName,
	String writer,
	String title,
	String content,
	Long recommendationCount,
	Long viewCount,
	LocalDateTime createdAt,
	Boolean isNotification,
	LocalDateTime startTime,
	LocalDateTime endTime,
	List<GetImageResponse> images,
	String gradeName,
	Boolean isAuthorized,
	Boolean isFavorite) {

	public static GetPostResponse fromEntity(Post post, List<GetImageResponse> images, Boolean isAuthorized,
		Boolean isFavorite) {
		return GetPostResponse.builder()
			.postId(post.getPostId())
			.categoryName(post.getCategory().getName())
			.writer(post.getWriter())
			.title(post.getTitle())
			.content(post.getContent())
			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(LocalDateTime.now())
			.isNotification(post.getIsNotification())
			.startTime(post.getStartTime())
			.endTime(post.getEndTime())
			.images(images)
			.gradeName(post.getUser().getGrade().getName())
			.isAuthorized(isAuthorized)
			.isFavorite(isFavorite)
			.build();
	}
}
