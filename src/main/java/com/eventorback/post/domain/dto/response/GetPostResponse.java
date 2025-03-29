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
	String statusName,
	String writer,
	String writerGrade,
	String title,
	String content,
	Long recommendationCount,
	Long viewCount,
	LocalDateTime createdAt,

	String link,

	LocalDateTime startTime,
	LocalDateTime endTime,
	String endType,

	String shoppingMall,
	String productName,
	Long price,

	List<GetImageResponse> images,
	Integer attachmentImageCount,
	Double totalSize,
	Boolean isAuthorized,
	Boolean isFavorite) {

	public static GetPostResponse fromEntity(Post post, List<GetImageResponse> images, Boolean isAuthorized,
		Boolean isFavorite) {
		Integer attachmentImageCount = images != null ? images.size() : 0;
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
		String endType = null;

		if (post.getEvent() != null) {
			link = post.getEvent().getLink();
			startTime = post.getEvent().getStartTime();
			endTime = post.getEvent().getEndTime();
			endType = post.getEvent().getEndType();
		}

		return GetPostResponse.builder()
			.postId(post.getPostId())
			.categoryName(post.getCategory().getName())
			.statusName(post.getStatus().getName())
			.writer(post.getWriter())
			.writerGrade(post.getWriterGrade())
			.title(post.getTitle())
			.content(post.getContent())
			.recommendationCount(post.getRecommendationCount())
			.viewCount(post.getViewCount())
			.createdAt(post.getCreatedAt())

			.link(link)

			.startTime(startTime)
			.endTime(endTime)
			.endType(endType)

			.shoppingMall(shoppingMall)
			.productName(productName)
			.price(price)

			.images(images)
			.attachmentImageCount(attachmentImageCount)
			.totalSize(totalSize)
			.isAuthorized(isAuthorized)
			.isFavorite(isFavorite)
			.build();
	}
}
