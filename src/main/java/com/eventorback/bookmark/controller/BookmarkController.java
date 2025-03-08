package com.eventorback.bookmark.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.bookmark.domain.dto.response.GetBookmarkResponse;
import com.eventorback.bookmark.service.BookmarkService;
import com.eventorback.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class BookmarkController {
	private final BookmarkService bookmarkService;

	@GetMapping("/users/me/bookmarks")
	public ApiResponse<List<GetBookmarkResponse>> getBookmarksByUserId(@CurrentUserId Long userId) {
		return ApiResponse.createSuccess(bookmarkService.getBookmarksByUserId(userId));
	}

	@AuthorizeRole("member")
	@GetMapping("/users/me/bookmarks/paging")
	public ApiResponse<Page<GetBookmarkResponse>> getBookmarksByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ApiResponse.createSuccess(bookmarkService.getBookmarksByUserId(pageable, userId));
	}

	@PostMapping("/categories/{categoryName}/bookmarks")
	public ApiResponse<Void> createOrDeleteBookmark(@CurrentUserId Long userId,
		@PathVariable String categoryName) {
		return ApiResponse.createSuccess(bookmarkService.createOrDeleteBookmark(userId, categoryName));
	}

	@DeleteMapping("/bookmarks/{bookmarkId}")
	public ApiResponse<Void> deleteBookmark(@CurrentUserId Long userId, @PathVariable Long bookmarkId) {
		bookmarkService.deleteBookmark(userId, bookmarkId);
		return ApiResponse.createSuccess("즐겨찾기를 삭제했습니다.");
	}
}
