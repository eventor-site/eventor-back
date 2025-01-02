package com.eventorback.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.comment.domain.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findAllByPostPostId(Long postId);
}