package com.eventorback.commentRecommend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.commentRecommend.domain.entity.CommentRecommend;

public interface CommentRecommendRepository extends JpaRepository<CommentRecommend, Long> {

	boolean existsByUserUserIdAndPostPostId(Long userId, Long postId);
}