package com.eventorback.global.aspect;

import java.time.Duration;
import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.eventorback.global.annotation.TimedExecution;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

	@Around("@annotation(timedExecution)")
	public Object measureExecutionTime(ProceedingJoinPoint joinPoint, TimedExecution timedExecution) throws Throwable {
		Instant start = Instant.now();

		Object result = joinPoint.proceed();

		Instant end = Instant.now();
		long durationSeconds = Duration.between(start, end).toSeconds();

		String tag = timedExecution.value();
		if (tag.isBlank()) {
			tag = joinPoint.getSignature().toShortString();
		}

		log.info("[{}] 실행 완료, 소요 시간: {}초", tag, durationSeconds);

		return result;
	}
}
