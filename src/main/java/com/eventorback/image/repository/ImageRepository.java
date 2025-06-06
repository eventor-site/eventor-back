package com.eventorback.image.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.image.domain.entity.Image;
import com.eventorback.post.domain.entity.Post;

public interface ImageRepository extends JpaRepository<Image, Long>, CustomImageRepository {

	Optional<Image> findByPostPostIdAndIsThumbnail(Long postId, Boolean isThumbnail);

	boolean existsByPostPostIdAndIsThumbnail(Long postId, Boolean isThumbnail);

	Optional<Image> findTopByPostPostIdOrderByImageIdAsc(Long postId);

	List<Image> findByPostUserUserIdAndPostStatusName(Long userId, String statusName);

	void deleteByPostPostIdAndIsThumbnail(Long postId, Boolean isThumbnail);

	Long post(Post post);
}