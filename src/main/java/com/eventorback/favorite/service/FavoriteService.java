package com.eventorback.favorite.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.favorite.domain.dto.response.GetFavoriteResponse;

public interface FavoriteService {

	Page<GetFavoriteResponse> getFavoritesByUserId(Pageable pageable, Long userId);

	String createOrDeleteFavorite(Long userId, Long postId);

	String deleteFavorite(Long userId, Long favoriteId);
}
