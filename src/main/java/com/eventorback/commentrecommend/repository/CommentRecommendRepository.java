package com.eventorback.commentrecommend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.commentrecommend.domain.entity.CommentRecommend;

public interface CommentRecommendRepository extends JpaRepository<CommentRecommend, Long> {

	Optional<CommentRecommend> findByUserUserIdAndCommentCommentId(Long userId, Long commentId);
}