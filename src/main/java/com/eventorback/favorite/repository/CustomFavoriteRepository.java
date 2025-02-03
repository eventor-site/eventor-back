package com.eventorback.favorite.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.favorite.domain.dto.response.GetFavoriteResponse;

public interface CustomFavoriteRepository {
	Page<GetFavoriteResponse> getFavoritePosts(Pageable pageable, Long userId);
}