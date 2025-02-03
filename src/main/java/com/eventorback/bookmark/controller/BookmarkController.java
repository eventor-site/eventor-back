package com.eventorback.bookmark.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.bookmark.domain.dto.response.GetBookmarkResponse;
import com.eventorback.bookmark.service.BookmarkService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class BookmarkController {
	private final BookmarkService bookmarkService;

	@GetMapping("/users/me/bookmarks")
	public ResponseEntity<List<GetBookmarkResponse>> getBookmarksByUserId(@CurrentUserId Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(bookmarkService.getBookmarksByUserId(userId));
	}

	@GetMapping("/users/me/bookmarks/paging")
	public ResponseEntity<Page<GetBookmarkResponse>> getBookmarksByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(bookmarkService.getBookmarksByUserId(pageable, userId));
	}

	@PostMapping("/categories/{categoryName}/bookmarks")
	public ResponseEntity<String> createOrDeleteBookmark(@CurrentUserId Long userId,
		@PathVariable String categoryName) {
		return ResponseEntity.status(HttpStatus.OK).body(bookmarkService.createOrDeleteBookmark(userId, categoryName));
	}

	@DeleteMapping("/bookmarks/{bookmarkId}")
	public ResponseEntity<String> deleteBookmark(@CurrentUserId Long userId, @PathVariable Long bookmarkId) {
		return ResponseEntity.status(HttpStatus.OK).body(bookmarkService.deleteBookmark(userId, bookmarkId));
	}
}
