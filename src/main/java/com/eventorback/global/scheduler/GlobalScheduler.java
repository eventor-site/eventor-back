package com.eventorback.global.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventorback.global.config.LeaderElectionManager;
import com.eventorback.post.service.PostService;
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
	private final LeaderElectionManager leaderElectionManager;
	private final PostService postService;
	private final UserService userService;

	/**
	 * 메인페이지 캐싱 제거
	 */
	@Scheduled(cron = "0 */10 * * * *")
	public void evictMainPageCache() {
		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		postService.evictMainPageCache();
	}

	/**
	 * 매일 자정에 실행되어 90일 이상 로그인하지 않은 사용자들의 상태를 '휴면' 으로 업데이트 합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void updateDormantUsersScheduler() {

		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		userService.updateDormantUsers();
	}

	/**
	 * 매일 자정을 기준으로 정지 종료 시간이 지난 계정들을 활성화 시킵니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void activeScheduler() {

		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		userService.unlockExpiredStopUsers();
	}

	/**
	 * 매일 자정에 실행되어 만료된 게시물을 삭제합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void deleteExpiredPostsScheduler() {

		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		postService.deleteExpiredPosts();

	}

	/**
	 * 매일 자정에 실행되어 만료된 계정을 Soft Delete 처리 합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void softDeleteExpiredUsersScheduler() {

		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		userService.softDeleteExpiredUsers();
	}

}
