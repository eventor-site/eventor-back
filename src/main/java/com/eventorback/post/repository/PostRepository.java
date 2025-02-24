package com.eventorback.post.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.post.domain.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, CustomPostRepository {

	Boolean existsByPostIdAndUserUserId(Long postId, Long userId);

	void deleteAllByUserUserIdAndStatusName(Long postId, String statusName);

}