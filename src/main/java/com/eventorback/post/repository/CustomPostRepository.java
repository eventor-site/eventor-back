package com.eventorback.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.entity.Post;

public interface CustomPostRepository {

	List<GetPostSimpleResponse> getPosts();

	Page<GetPostSimpleResponse> getPosts(Pageable pageable);

	List<GetPostSimpleResponse> getPostsByUserId(Long userId);

	List<GetMainPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetMainPostResponse> getRecommendationEventPosts();

	List<GetMainPostResponse> getHotPostsByCategoryName(List<Long> categoryIds);

	List<GetPostsByCategoryNameResponse> getPostsByCategoryName(List<Long> categoryIds);

	Optional<Post> getPost(Long postId);
}
