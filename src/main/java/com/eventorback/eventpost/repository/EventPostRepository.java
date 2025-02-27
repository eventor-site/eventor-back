package com.eventorback.eventpost.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.eventpost.domain.entity.EventPost;

public interface EventPostRepository extends JpaRepository<EventPost, Long> {

	Optional<EventPost> findByPostPostId(Long postId);
}