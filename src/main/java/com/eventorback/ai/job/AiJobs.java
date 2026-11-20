package com.eventorback.ai.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventorback.ai.service.InsightPipeline;
import com.eventorback.ai.service.WeeklyReportService;
import com.eventorback.llm.LlmService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * Gemini 자동 작업. 인스턴스가 여러 개여도(블루/그린) ShedLock 으로 하나만 돈다.
 * 3분마다 분류 1요청 + 링크 읽기 1요청 → 이벤터 몫(분당 4, 하루 200)은 LlmService 가 지킨다.
 * 새 글·고친 글은 입력 해시가 바뀌어 다음 주기에 자동으로 다시 분석된다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiJobs {
	private final LlmService llm;
	private final InsightPipeline pipeline;
	private final WeeklyReportService reports;

	@Scheduled(fixedDelay = 180_000, initialDelay = 90_000)
	@SchedulerLock(name = "ai.insights", lockAtMostFor = "10m")
	public void analyze() {
		if (!llm.jobsEnabled()) {
			return;
		}
		try {
			var c = pipeline.classifyPending(1);
			var d = pipeline.deepPending(1);
			if (c.requests() + d.requests() > 0) {
				log.info("AI 분석: 분류 {}건 저장, 링크 읽기 {}건 저장", c.saved(), d.saved());
			}
		} catch (Exception e) {
			log.warn("AI 분석 작업 실패", e);
		}
	}

	/** 매주 월요일 07:00(한국) 주간 리포트. 실패하면 같은 날 다음 실행(10시)에서 다시 */
	@Scheduled(cron = "0 0 7,10 * * MON", zone = "Asia/Seoul")
	@SchedulerLock(name = "ai.weeklyReport", lockAtMostFor = "10m")
	public void weeklyReport() {
		if (!llm.jobsEnabled()) {
			return;
		}
		try {
			reports.generate(false);
		} catch (Exception e) {
			log.warn("주간 리포트 실패", e);
		}
	}
}
