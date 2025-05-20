package com.eventorback.global.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventorback.global.annotation.TimedExecution;
import com.eventorback.global.config.LeaderElectionManager;
import com.eventorback.post.service.PostService;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.repository.UserRepository;
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
	private final UserRepository userRepository;
	private final StatusRepository statusRepository;
	private final PostService postService;
	private final UserService userService;

	/**
	 * 매일 자정에 실행되어 90일 이상 로그인하지 않은 사용자들의 상태를 '휴면'으로 업데이트합니다.
	 */
	@TimedExecution("휴면 계정 스케줄러")
	@Scheduled(cron = "0 0 0 * * *")    // 매일 자정 마다 실행
	public void dormantScheduler() {

		// 리더가 아니면 실행 X
		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		Status dormantStatus = statusRepository.findOrCreateStatus("회원", "휴면");
		List<Long> userIds = userRepository.getDormantUsers();

		if (!userIds.isEmpty()) {
			userRepository.updateUserStatusToDormant(userIds, dormantStatus);
		}
	}

	/**
	 * 매일 자정을 기준으로 정지 종료 시간이 지난 계정들을 활성화 시킵니다.
	 */
	@TimedExecution("정지 계정 정상화 스케줄러")
	@Scheduled(cron = "0 0 0 * * *")
	public void activeScheduler() {

		// 리더가 아니면 실행 X
		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		List<Long> userIds = userRepository.getStopUsers();
		Status activeStatus = statusRepository.findOrCreateStatus("회원", "활성");

		if (!userIds.isEmpty()) {
			userRepository.updateUserStatusToActive(userIds, activeStatus);
		}
	}

	/**
	 * 매일 자정에 실행되어 만료된 게시물을 삭제합니다.
	 */
	@TimedExecution("만료된 게시물을 삭제 스케줄러")
	@Scheduled(cron = "0 0 0 * * *")    // 매일 자정 마다 실행
	public void deleteExpiredPostsScheduler() {

		// 리더가 아니면 실행 X
		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		postService.deleteExpiredPosts();

	}

	/**
	 * 매일 자정에 실행되어 만료된 계정을 Soft Delete 처리 합니다.
	 */
	@TimedExecution("만료된 계정 Soft Delete 스케줄러")
	@Scheduled(cron = "0 0 0 * * *")    // 매일 자정 마다 실행
	public void softDeleteExpiredUsersScheduler() {

		// 리더가 아니면 실행 X
		if (!leaderElectionManager.tryAcquireLeadership()) {
			return;
		}

		userService.softDeleteExpiredUsers();
	}

}
