package com.eventorback.favorite.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.favorite.domain.dto.response.GetFavoriteResponse;
import com.eventorback.favorite.service.FavoriteService;
import com.eventorback.global.dto.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class FavoriteController {
	private final FavoriteService favoriteService;

	@AuthorizeRole("member")
	@GetMapping("/users/me/favorites/paging")
	public ResponseEntity<ApiResponse<Page<GetFavoriteResponse>>> getFavoritesByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ApiResponse.createSuccess(favoriteService.getFavoritesByUserId(pageable, userId));
	}

	@PostMapping("/post/{postId}/favorites")
	public ResponseEntity<ApiResponse<Void>> createOrDeleteFavorite(@CurrentUserId Long userId,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(favoriteService.createOrDeleteFavorite(userId, postId));
	}

	@DeleteMapping("/{favoriteId}")
	public ResponseEntity<ApiResponse<String>> deleteFavorite(@CurrentUserId Long userId,
		@PathVariable Long favoriteId) {
		favoriteService.deleteFavorite(userId, favoriteId);
		return ApiResponse.createSuccess("하트가 삭제되었습니다.");
	}
}
