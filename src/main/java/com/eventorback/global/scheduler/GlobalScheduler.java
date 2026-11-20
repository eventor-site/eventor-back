package com.eventorback.global.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import com.eventorback.post.service.PostService;
import com.eventorback.tour.service.TourService;
import com.eventorback.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 비활성 사용자 상태를 처리하기 위한 스케줄러 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GlobalScheduler {
	private final PostService postService;
	private final UserService userService;
	private final TourService tourService;

	/**
	 * 메인페이지 캐싱 제거. 캐시가 인스턴스마다 따로 있으므로(Caffeine) 락 없이 모든 인스턴스에서 실행한다
	 */
	@Scheduled(cron = "0 */10 * * * *")
	public void evictMainPageCache() {
		postService.evictMainPageCache();
	}

	/**
	 * 축제 정보 캐싱 제거(모든 인스턴스에서 실행)
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void evictSearchFestival2Cache() {
		tourService.evictSearchFestival2Cache();
	}

	/**
	 * 매일 자정에 실행되어 1년 이상 로그인하지 않은 사용자들의 상태를 '휴면' 으로 업데이트 합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	@SchedulerLock(name = "global.updateDormantUsersScheduler", lockAtMostFor = "30m")
	public void updateDormantUsersScheduler() {

		userService.updateDormantUsers();
	}

	/**
	 * 매일 자정을 기준으로 정지 종료 시간이 지난 계정들을 활성화 시킵니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	@SchedulerLock(name = "global.activeScheduler", lockAtMostFor = "30m")
	public void activeScheduler() {

		userService.unlockExpiredStopUsers();
	}

	/**
	 * 매일 자정에 실행되어 만료된 게시물을 삭제합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	@SchedulerLock(name = "global.deleteExpiredPostsScheduler", lockAtMostFor = "30m")
	public void deleteExpiredPostsScheduler() {

		postService.deleteExpiredPosts();

	}

	/**
	 * 매일 자정에 실행되어 만료된 계정을 Soft Delete 처리 합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	@SchedulerLock(name = "global.softDeleteExpiredUsersScheduler", lockAtMostFor = "30m")
	public void softDeleteExpiredUsersScheduler() {

		userService.softDeleteExpiredUsers();
	}

}
