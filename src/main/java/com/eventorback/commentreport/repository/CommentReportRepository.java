package com.eventorback.commentreport.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.commentreport.domain.entity.CommentReport;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long>, CommentReportCustomRepository {

	Optional<CommentReport> findByUserUserIdAndCommentCommentId(Long userId, Long commentId);

	boolean existsByUserUserIdAndCommentCommentId(Long userId, Long commentId);

	void deleteByUserUserIdAndCommentCommentId(Long userId, Long commentId);
}