package com.eventorback.bookmark.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.bookmark.domain.dto.response.GetBookmarkResponse;

public interface CustomBookmarkRepository {
	List<GetBookmarkResponse> getBookmarksByUserId(Long userId);

	Page<GetBookmarkResponse> getBookmarksByUserId(Pageable pageable, Long userId);
}