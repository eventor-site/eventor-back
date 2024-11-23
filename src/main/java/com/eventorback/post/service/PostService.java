package com.eventorback.post.service;

import java.util.List;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface PostService {

	List<GetPostSimpleResponse> getPosts();

	List<GetPostSimpleResponse> getHotEventPosts();

	List<GetPostSimpleResponse> getLatestEventPosts();

	List<GetPostSimpleResponse> getRecommendationEventPosts();

	List<GetPostSimpleResponse> getPostsByCategoryName(String categoryName);

	GetPostResponse getPost(CurrentUserDto currentUser, Long postId);

	CreatePostResponse createPost(Long userId, CreatePostRequest request);

	void updatePost(Long userId, Long postId, UpdatePostRequest request);

	void deletePost(Long postId);
}
