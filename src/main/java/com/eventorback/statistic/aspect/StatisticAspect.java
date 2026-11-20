package com.eventorback.statistic.aspect;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.statistic.domain.entity.Statistic;
import com.eventorback.statistic.repository.StatisticRepository;
import com.eventorback.user.repository.UserRepository;
import com.eventorback.user.domain.dto.request.UpdateLoginAtRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Transactional
public class StatisticAspect {
	private final StatisticRepository statisticRepository;
	private final UserRepository userRepository;

	@AfterReturning("execution(* com.eventorback.post.controller.PostController.getHotEventPosts(..))")
	public void collectInfoAfterReturningGetMainPage() {
		Statistic statistic = statisticRepository.findByDate(LocalDate.now())
			.orElseGet(() -> statisticRepository.save(new Statistic()));
		statistic.increaseMainViewCount();
		statisticRepository.save(statistic);
	}

	@AfterReturning("execution(* com.eventorback.postview.repository.PostViewRepository.save(..))")
	public void collectInfoAfterReturningGetPostView() {
		Statistic statistic = statisticRepository.findByDate(LocalDate.now())
			.orElseGet(() -> statisticRepository.save(new Statistic()));
		statistic.increasePostViewCount();
		statisticRepository.save(statistic);
	}

	@AfterReturning("execution(* com.eventorback.user.controller.UserController.signup(..))")
	public void collectInfoAfterReturningSignup() {
		Statistic statistic = statisticRepository.findByDate(LocalDate.now())
			.orElseGet(() -> statisticRepository.save(new Statistic()));
		statistic.increaseSignupCount();
		statisticRepository.save(statistic);
	}

	/**
	 * 오늘 로그인한 회원 수. 로그인할 때 users.login_at이 오늘로 바뀌므로 그 수를 센다(이전에는 Redis에 ID 목록을 두었다).
	 */
	@AfterReturning("execution(* com.eventorback.user.service.impl.UserServiceImpl.updateLoginAt(..))")
	public void collectInfoAfterReturningLoginMemberCount() {
		long loginCount = userRepository.countByLoginAtGreaterThanEqual(LocalDate.now().atStartOfDay());
		Statistic statistic = statisticRepository.findByDate(LocalDate.now())
			.orElseGet(() -> statisticRepository.save(new Statistic()));
		statistic.updateLoginCount(loginCount);
		statisticRepository.save(statistic);
	}
}

