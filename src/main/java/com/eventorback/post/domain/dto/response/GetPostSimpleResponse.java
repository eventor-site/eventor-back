package com.eventorback.post.domain.dto.response;

import java.time.LocalDateTime;

import com.eventorback.post.domain.entity.Post;

import lombok.Builder;

@Builder
public record GetPostSimpleResponse(
	Long postId,
	String writer,
	String title,
	Long recommendationCount,
	Long viewCount,
	LocalDateTime createdAt,
	String gradeName) {

	public static GetPostSimpleResponse fromEntity(Post post) {
		return GetPostSimpleResponse.builder()
			.postId(post.getPostId())
			.writer(post.getWriter())
			.title(post.getTitle())
			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(post.getCreatedAt())
			.gradeName(post.getUser().getGrade().getName())
			.build();
	}
}
