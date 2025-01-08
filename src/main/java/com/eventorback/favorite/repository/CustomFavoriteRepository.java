package com.eventorback.favorite.repository;

import java.util.List;

import com.eventorback.favorite.domain.dto.response.GetFavoriteResponse;

public interface CustomFavoriteRepository {
	List<GetFavoriteResponse> getFavoritePosts(Long userId);
}