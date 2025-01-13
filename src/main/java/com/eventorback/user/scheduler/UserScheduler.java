package com.eventorback.user.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.userstop.domain.entity.UserStop;
import com.eventorback.userstop.repository.UserStopRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 비활성 사용자 상태를 처리하기 위한 스케줄러 클래스입니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserScheduler {
	private final UserRepository userRepository;
	private final StatusRepository statusRepository;
	private final UserStopRepository userStopRepository;

	/**
	 * 매일 자정에 실행되어 90일 이상 로그인하지 않은 사용자들의 상태를 'DORMANT'로 업데이트합니다.
	 * <p>
	 * 이 메서드는 다음과 같은 절차를 따릅니다:
	 * <ul>
	 *     <li>모든 사용자를 조회합니다.</li>
	 *     <li>'휴면' 상태가 존재하지 않으면 새로 생성합니다.</li>
	 *     <li>마지막 로그인 시점이 현재 시점에서 90일을 초과하고 현재 상태가 '활성'인 사용자를 '휴면' 상태로 변경합니다.</li>
	 *     <li>상태가 변경된 사용자 정보를 저장합니다.</li>
	 * </ul>
	 */
	@Scheduled(cron = "0 0 0 * * *")    // 매일 자정 마다 실행
	public void dormantScheduler() {
		List<User> users = userRepository.findAll();
		// UserStatus dormantUserStatus = userStatusRepository.findByUserStatusName("DORMANT")
		// 	.orElseGet(() -> userStatusRepository.save(UserStatus.builder().userStatusName("DORMANT").build()));
		Status status = statusRepository.findOrCreateStatus("회원", "휴면");

		for (User user : users) {
			if (user.getLastLoginTime().isBefore(LocalDateTime.now().minusDays(90))
				&& user.getStatus().getName().equals("활성")) {
				user.updateStatus(status);
			}
		}
	}

	/**
	 * 매일 자정을 기준으로 정지 종료 시간이 지난 계정들을 활성화 시킵니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void activeScheduler() {
		List<UserStop> userStops = userStopRepository.findAll();

		Status status = statusRepository.findOrCreateStatus("회원", "활성");

		for (UserStop userStop : userStops) {
			if (userStop.getEndTime().isBefore(LocalDateTime.now()) && userStop.getUser()
				.getStatus().getName().equals("정지")) {
				userStop.getUser().updateStatus(status);
			}
		}
	}
}
