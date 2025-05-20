package com.eventorback.crawler.domain.dto.response;

import lombok.Builder;

@Builder
public record CrawlFmkoreaItemResponse(
	String title,
	String url) {
}
