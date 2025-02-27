package com.eventorback.post.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.eventorback.eventpost.domain.entity.EventPost;
import com.eventorback.hotdealpost.domain.entity.HotDealPost;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.post.domain.entity.Post;

import lombok.Builder;

@Builder
public record GetPostResponse(
	Long postId,
	String categoryName,
	String writer,
	String title,
	String link,
	String shoppingMall,
	String productName,
	Long price,
	String content,
	Long recommendationCount,
	Long viewCount,
	LocalDateTime createdAt,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String statusName,
	List<GetImageResponse> images,
	Double totalSize,
	String gradeName,
	Boolean isAuthorized,
	Boolean isFavorite) {

	public static GetPostResponse fromEntity(Post post, List<GetImageResponse> images, Boolean isAuthorized,
		Boolean isFavorite) {
		double totalSize = 0;

		if (images != null) {
			totalSize = images.stream()
				.mapToLong(GetImageResponse::size)
				.sum();
		}

		return GetPostResponse.builder()
			.postId(post.getPostId())
			.categoryName(post.getCategory().getName())
			.writer(post.getWriter())
			.title(post.getTitle())
			.content(post.getContent())
			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(LocalDateTime.now())
			.statusName(post.getStatus().getName())
			.images(images)
			.totalSize(totalSize)
			.gradeName(post.getUser().getGrade().getName())
			.isAuthorized(isAuthorized)
			.isFavorite(isFavorite)
			.build();
	}

	public static GetPostResponse fromEntity(Post post, HotDealPost hotDealPost, List<GetImageResponse> images,
		Boolean isAuthorized,
		Boolean isFavorite) {
		double totalSize = 0;

		if (images != null) {
			totalSize = images.stream()
				.mapToLong(GetImageResponse::size)
				.sum();
		}

		return GetPostResponse.builder()
			.postId(post.getPostId())
			.categoryName(post.getCategory().getName())
			.writer(post.getWriter())
			.title(post.getTitle())
			.link(hotDealPost.getLink())
			.shoppingMall(hotDealPost.getShoppingMall())
			.productName(hotDealPost.getProductName())
			.price(hotDealPost.getPrice())
			.content(post.getContent())
			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(LocalDateTime.now())
			.statusName(post.getStatus().getName())
			.images(images)
			.totalSize(totalSize)
			.gradeName(post.getUser().getGrade().getName())
			.isAuthorized(isAuthorized)
			.isFavorite(isFavorite)
			.build();
	}

	public static GetPostResponse fromEntity(Post post, EventPost eventPost, List<GetImageResponse> images,
		Boolean isAuthorized,
		Boolean isFavorite) {
		double totalSize = 0;

		if (images != null) {
			totalSize = images.stream()
				.mapToLong(GetImageResponse::size)
				.sum();
		}

		return GetPostResponse.builder()
			.postId(post.getPostId())
			.categoryName(post.getCategory().getName())
			.writer(post.getWriter())
			.title(post.getTitle())
			.link(eventPost.getLink())
			.content(post.getContent())
			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(LocalDateTime.now())
			.startTime(eventPost.getStartTime())
			.endTime(eventPost.getEndTime())
			.statusName(post.getStatus().getName())
			.images(images)
			.totalSize(totalSize)
			.gradeName(post.getUser().getGrade().getName())
			.isAuthorized(isAuthorized)
			.isFavorite(isFavorite)
			.build();
	}
}
