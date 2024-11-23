package com.eventorback.post.repository;

import java.util.List;
import java.util.Optional;

import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;

public interface CustomPostRepository {

	List<GetPostSimpleResponse> getPosts();

	List<GetPostSimpleResponse> getHotEventPosts();

	List<GetPostSimpleResponse> getLatestEventPosts();

	List<GetPostSimpleResponse> getRecommendationEventPosts();

	List<GetPostSimpleResponse> getPostsByCategoryName(String categoryName);

	Optional<GetPostResponse> getPost(Long postId);
}
