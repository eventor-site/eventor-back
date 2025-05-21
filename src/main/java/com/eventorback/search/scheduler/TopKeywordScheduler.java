package com.eventorback.search.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventorback.global.config.LeaderElectionManager;
import com.eventorback.search.service.impl.KeywordService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TopKeywordScheduler {
	private final LeaderElectionManager leaderElectionManager;
	private final KeywordService keywordService;

	@Scheduled(cron = "0 0 0 * * *")
	public void cleanUpOldTopKeywords() {

		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		keywordService.cleanUpOldTopKeywords();

	}

	@Scheduled(cron = "0 */5 * * * *")
	public void updateTopKeywords() {

		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		keywordService.updateTopKeywords();
	}

}
