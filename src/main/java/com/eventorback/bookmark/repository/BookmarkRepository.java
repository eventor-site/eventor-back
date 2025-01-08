package com.eventorback.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.eventorback.bookmark.domain.entity.Bookmark;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long>, CustomBookmarkRepository {

	boolean existsByUserUserIdAndCategoryName(Long userId, String categoryName);

	void deleteByUserUserIdAndCategoryName(Long userId, String categoryName);

	Optional<Bookmark> findByUserUserIdAndCategoryName(Long userId, String categoryName);
}