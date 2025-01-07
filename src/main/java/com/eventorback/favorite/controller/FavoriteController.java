package com.eventorback.favorite.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.favorite.service.FavoriteService;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class FavoriteController {
	private final FavoriteService favoriteService;

	@GetMapping("/users/me/favorites")
	public ResponseEntity<List<GetPostSimpleResponse>> getFavoritesByUserId(@CurrentUserId Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(favoriteService.getFavoritesByUserId(userId));
	}

	@PostMapping("/post/{postId}/favorites")
	public ResponseEntity<String> createFavorite(@CurrentUserId Long userId, @PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(favoriteService.createFavorite(userId, postId));
	}

	@DeleteMapping("/{favoriteId}")
	public ResponseEntity<String> deleteFavorite(@CurrentUserId Long userId, @PathVariable Long favoriteId) {
		return ResponseEntity.status(HttpStatus.OK).body(favoriteService.deleteFavorite(userId, favoriteId));
	}
}
