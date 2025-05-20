package com.eventorback.crawler.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventorback.crawler.service.CrawlerService;
import com.eventorback.global.annotation.TimedExecution;
import com.eventorback.global.config.LeaderElectionManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CrawlerScheduler {
	private final LeaderElectionManager leaderElectionManager;
	private final CrawlerService crawlerService;

	@TimedExecution("에펨코리아 핫딜 게시물 크롤링 스케줄러")
	@Scheduled(cron = "0 */5 * * * *")
	public void crawlHotDeals() {

		// 리더가 아니면 실행 X
		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		crawlerService.refreshAndSaveNewItems();
	}
}
