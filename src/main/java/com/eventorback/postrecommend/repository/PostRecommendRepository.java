package com.eventorback.postrecommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.postrecommend.domain.entity.PostRecommend;

public interface PostRecommendRepository extends JpaRepository<PostRecommend, Long> {

	boolean existsByUserUserIdAndPostPostId(Long userId, Long postId);
}