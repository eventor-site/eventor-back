package com.eventorback.favorite.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.favorite.domain.entity.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

	boolean existsByUserUserIdAndPostPostId(Long userId, Long postId);

	void deleteByUserUserIdAndPostPostId(Long userId, Long postId);

	Optional<Favorite> findByUserUserIdAndPostPostId(Long userId, Long postId);
}