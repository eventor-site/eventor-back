package com.eventorback.postview.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.postview.domain.entity.PostView;

public interface PostViewRepository extends JpaRepository<PostView, Long> {

	boolean existsByViewerIdAndPostPostId(String viewerId, Long postId);
}