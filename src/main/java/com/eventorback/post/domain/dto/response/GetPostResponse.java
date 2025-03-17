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
	String writerGrade,
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
	Integer attachmentImageCount,
	Double totalSize,
	Boolean isAuthorized,
	Boolean isFavorite) {

	public static GetPostResponse fromEntity(Post post, List<GetImageResponse> images, Boolean isAuthorized,
		Boolean isFavorite) {
		Integer attachmentImageCount = images.stream().filter(image -> !image.isThumbnail()).toList().size();
		double totalSize = 0;

		if (images != null) {
			totalSize = images.stream()
				.mapToLong(GetImageResponse::size)
				.sum();
		}

		String link = null;
		String shoppingMall = null;
		String productName = null;
		Long price = null;

		if (post.getHotDeal() != null) {
			link = post.getHotDeal().getLink();
			shoppingMall = post.getHotDeal().getShoppingMall();
			productName = post.getHotDeal().getProductName();
			price = post.getHotDeal().getPrice();
		}

		LocalDateTime startTime = null;
		LocalDateTime endTime = null;
		if (post.getEvent() != null) {
			link = post.getEvent().getLink();
			startTime = post.getEvent().getStartTime();
			endTime = post.getEvent().getEndTime();
		}

		return GetPostResponse.builder()
			.postId(post.getPostId())
			.categoryName(post.getCategory().getName())
			.writer(post.getWriter())
			.writerGrade(post.getWriterGrade())
			.title(post.getTitle())
			.content(post.getContent())

			.link(link)

			.shoppingMall(shoppingMall)
			.productName(productName)
			.price(price)

			.startTime(startTime)
			.endTime(endTime)

			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(post.getCreatedAt())
			.statusName(post.getStatus().getName())
			.images(images)
			.attachmentImageCount(attachmentImageCount)
			.totalSize(totalSize)
			.isAuthorized(isAuthorized)
			.isFavorite(isFavorite)
			.build();
	}
}
