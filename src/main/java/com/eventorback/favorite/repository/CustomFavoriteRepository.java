package com.eventorback.favorite.repository;

import java.util.List;

import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;

public interface CustomFavoriteRepository {
	List<GetPostSimpleResponse> getFavoritePosts(Long userId);
}