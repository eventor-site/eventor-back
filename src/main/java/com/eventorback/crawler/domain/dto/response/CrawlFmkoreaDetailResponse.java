package com.eventorback.crawler.domain.dto.response;

import java.util.List;

import lombok.Builder;

@Builder
public record CrawlFmkoreaDetailResponse(
	String url,
	String title,
	String link,
	String shoppingMall,
	String productName,
	Long price,
	String content,
	List<String> images
) {
}
