package com.eventorback.hotdealpost.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.hotdealpost.domain.entity.HotDealPost;

public interface HotDealPostRepository extends JpaRepository<HotDealPost, Long> {

	Optional<HotDealPost> findByPostPostId(Long postId);

}