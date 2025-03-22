package com.eventorback.user.scheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.eventorback.grade.repository.GradeRepository;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.repository.UserRepository;

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
	private final GradeRepository gradeRepository;

	@Value("${server.port}")
	private String port;

	/**
	 * 매일 자정에 실행되어 90일 이상 로그인하지 않은 사용자들의 상태를 '휴면'으로 업데이트합니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")    // 매일 자정 마다 실행
	public void dormantScheduler() {

		// 특정 포트(8101, 8103)에서만 실행
		if (!port.equals("8101") && !port.equals("8103")) {
			return;
		}

		Instant start = Instant.now();  // 시작 시간 기록

		Status dormantStatus = statusRepository.findOrCreateStatus("회원", "휴면");
		List<Long> userIds = userRepository.getDormantUsers();

		if (!userIds.isEmpty()) {
			userRepository.updateUserStatusToDormant(userIds, dormantStatus);
		}

		Instant end = Instant.now();  // 종료 시간 기록
		long durationSeconds = Duration.between(start, end).toSeconds();  // 걸린 시간 계산 (초 단위)

		log.info("휴면 계정 스케줄러 완료, 걸린 시간: {}초", durationSeconds);
	}

	/**
	 * 매일 자정을 기준으로 정지 종료 시간이 지난 계정들을 활성화 시킵니다.
	 */
	@Scheduled(cron = "0 0 0 * * *")
	public void activeScheduler() {

		// 특정 포트(8101, 8103)에서만 실행
		if (!port.equals("8101") && !port.equals("8103")) {
			return;
		}

		Instant start = Instant.now();  // 시작 시간 기록

		List<Long> userIds = userRepository.getStopUsers();
		Status activeStatus = statusRepository.findOrCreateStatus("회원", "활성");

		if (!userIds.isEmpty()) {
			userRepository.updateUserStatusToActive(userIds, activeStatus);
		}

		Instant end = Instant.now();  // 종료 시간 기록
		long durationSeconds = Duration.between(start, end).toSeconds();  // 걸린 시간 계산 (초 단위)

		log.info("정지 계정 정상화 스케줄러 완료, 걸린 시간: {}초", durationSeconds);
	}

	// /**
	//  * 매일 자정을 기준으로 포인트에 따라 회원 등급을 업데이트 합니다.
	//  */
	// @Scheduled(cron = "0 0 0 * * *")
	// public void upgradeScheduler() {
	//
	// 	// 특정 포트(8101, 8103)에서만 실행
	// 	if (!port.equals("8101") && !port.equals("8103")) {
	// 		return;
	// 	}
	//
	// 	Instant start = Instant.now();  // 시작 시간 기록
	//
	// 	List<Long> userIds = userRepository.getNotAdminUsers();
	// 	List<Grade> grades = gradeRepository.findAll();
	//
	// 	Instant end = Instant.now();  // 종료 시간 기록
	// 	long durationSeconds = Duration.between(start, end).toSeconds();  // 걸린 시간 계산 (초 단위)
	//
	// 	log.info("등급 업그레이드 스케줄러 완료, 걸린 시간: {}초", durationSeconds);
	// }
	//
	// /**
	//  * 포인트에 따라 적절한 등급을 결정하는 메서드
	//  */
	// private Grade determineGrade(Long point, List<Grade> grades) {
	// 	for (Grade grade : grades) {
	// 		if (point >= grade.getMinAmount() && (grade.getMaxAmount() == null || point <= grade.getMaxAmount())) {
	// 			return grade;
	// 		}
	// 	}
	// 	return grades.get(0); // 기본 등급 (첫 번째 등급)
	// }

}
