package com.eventorback.crawler.scheduler;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import com.eventorback.crawler.service.CrawlerService;

import lombok.RequiredArgsConstructor;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class CrawlerScheduler {
	private final CrawlerService crawlerService;

	// 크롤링 중단 상태(2025). 다시 켤 때 @Scheduled 주석을 푼다
	// @Scheduled(cron = "0 */30 * * * *")
	@SchedulerLock(name = "crawler.hotDeals", lockAtMostFor = "25m")
	public void crawlHotDeals() {

		crawlerService.refreshAndSaveNewItems();
	}
}
