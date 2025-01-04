package com.eventorback.commentRecommend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.commentRecommend.domain.entity.CommentRecommend;

public interface CommentRecommendRepository extends JpaRepository<CommentRecommend, Long> {

	Optional<CommentRecommend> findByUserUserIdAndCommentCommentId(Long userId, Long commentId);
}