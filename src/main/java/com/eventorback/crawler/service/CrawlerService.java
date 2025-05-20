package com.eventorback.crawler.service;

import java.util.List;

import com.eventorback.crawler.domain.dto.response.CrawlFmkoreaDetailResponse;
import com.eventorback.crawler.domain.dto.response.CrawlFmkoreaItemResponse;

public interface CrawlerService {

	void refreshAndSaveNewItems();

	List<CrawlFmkoreaItemResponse> crawlHotDealUntil(String lastUrl);

	CrawlFmkoreaDetailResponse parseDetail(String url);
}
