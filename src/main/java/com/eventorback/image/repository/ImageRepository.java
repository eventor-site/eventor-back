package com.eventorback.image.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.image.domain.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {

	Optional<Image> findTopByPostPostIdOrderByImageIdAsc(Long postId);

	List<Image> findAllByPostPostId(Long postId);

	void deleteAllByPostPostId(Long postId);
}