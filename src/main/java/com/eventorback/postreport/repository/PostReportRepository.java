package com.eventorback.postreport.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.postreport.domain.entity.PostReport;

public interface PostReportRepository extends JpaRepository<PostReport, Long>, PostReportCustomRepository {

	Optional<PostReport> findByUserUserIdAndPostPostId(Long userId, Long postId);

	boolean existsByUserUserIdAndPostPostId(Long userId, Long postId);

	void deleteByUserUserIdAndPostPostId(Long userId, Long postId);
}