package com.eventorback.bookmark.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.bookmark.domain.dto.response.GetBookmarkResponse;

public interface BookmarkService {

	List<GetBookmarkResponse> getBookmarksByUserId(Long userId);

	Page<GetBookmarkResponse> getBookmarksByUserId(Pageable pageable, Long userId);

	String createOrDeleteBookmark(Long userId, String categoryName);

	String deleteBookmark(Long userId, Long bookmarkId);
}
