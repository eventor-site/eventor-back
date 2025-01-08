package com.eventorback.bookmark.repository;

import java.util.List;

import com.eventorback.bookmark.domain.dto.response.GetBookmarkResponse;

public interface CustomBookmarkRepository {
	List<GetBookmarkResponse> getBookmarksByUserId(Long userId);
}