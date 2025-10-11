// package com.eventorback.crawler.scheduler;
//
// import org.springframework.context.annotation.Profile;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Component;
//
// import com.eventorback.crawler.service.CrawlerService;
// import com.eventorback.global.config.LeaderElectionManager;
//
// import lombok.RequiredArgsConstructor;
//
// @Profile("prod")
// @Component
// @RequiredArgsConstructor
// public class CrawlerScheduler {
// 	private final LeaderElectionManager leaderElectionManager;
// 	private final CrawlerService crawlerService;
//
// 	@Scheduled(cron = "0 */30 * * * *")
// 	public void crawlHotDeals() {
//
// 		if (!leaderElectionManager.tryAcquireLeadership()) {
// 			return;
// 		}
//
// 		crawlerService.refreshAndSaveNewItems();
// 	}
// }
