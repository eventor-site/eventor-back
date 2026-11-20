package com.eventorback.post.support;

import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.RequiredArgsConstructor;

/**
 * 글을 쓰거나 고치거나 지우면 메인 화면 목록·트렌드 통계 캐시를 비운다.
 * 이전에는 10분마다 도는 스케줄러만 비워서, 지운 글이 메인에 최대 10분 남았다(2026-11-20).
 * DB 커밋이 끝난 뒤에 비운다: 커밋 전에 비우면 그 사이 다른 요청이 옛 데이터를 다시 캐시에 넣을 수 있다.
 */
@Component
@RequiredArgsConstructor
public class PostCacheEvictor {
	private static final List<String> KEYS = List.of(
		"hotEventPosts", "latestEventPosts", "deadlineEventPosts", "recommendationEventPosts", "trendingEventPosts",
		"hotDealPosts", "communityPosts", "insightsOverview");

	private final CacheManager cacheManager;

	public void evictAfterCommit() {
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					evictNow();
				}
			});
		} else {
			evictNow();
		}
	}

	public void evictNow() {
		Cache cache = cacheManager.getCache("cache");
		if (cache != null) {
			KEYS.forEach(cache::evict);
		}
	}
}
