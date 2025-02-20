package com.eventorback.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.post.domain.entity.Post;

public interface CustomPostRepository {

	Page<GetPostSimpleResponse> getPosts(Pageable pageable);

	Page<GetPostSimpleResponse> getPostsByUserId(Pageable pageable, Long userId);

	List<GetMainPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetRecommendPostResponse> getRecommendationEventPosts();

	List<GetRecommendPostResponse> getTrendingEventPosts();

	List<GetMainPostResponse> getHotPostsByCategoryName(List<Long> categoryIds);

	Page<GetPostsByCategoryNameResponse> getPostsByEventCategory(Pageable pageable, List<Long> categoryIds);

	Page<GetPostsByCategoryNameResponse> getPostsByCategoryName(Pageable pageable, List<Long> categoryIds);

	Optional<Post> getPost(Long postId);
}
