package com.eventorback.postview.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.postview.domain.entity.PostView;

public interface PostViewRepository extends JpaRepository<PostView, Long>, CustomPostViewRepository {

	boolean existsByViewerIdAndPostPostId(String viewerId, Long postId);

	Optional<PostView> findByViewerIdAndPostPostId(String viewerId, Long postId);
}