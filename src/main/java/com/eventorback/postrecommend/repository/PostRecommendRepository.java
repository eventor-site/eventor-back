package com.eventorback.postrecommend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.postrecommend.domain.entity.PostRecommend;

public interface PostRecommendRepository extends JpaRepository<PostRecommend, Long> {

	Optional<PostRecommend> findByUserUserIdAndPostPostId(Long userId, Long postId);
}