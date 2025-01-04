package com.eventorback.post.service;

import java.util.List;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface PostService {

	List<GetPostSimpleResponse> getPosts();

	List<GetMainPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetMainPostResponse> getRecommendationEventPosts();

	GetPostsByCategoryNameResponse getPostsByCategoryName(CurrentUserDto currentUser, String categoryName);

	GetPostResponse getPost(CurrentUserDto currentUser, Long postId);

	CreatePostResponse createPost(Long userId, CreatePostRequest request);

	void updatePost(Long userId, Long postId, UpdatePostRequest request);

	String recommendPost(Long userId, Long postId);

	String disrecommendPost(Long userId, Long postId);

	void deletePost(Long postId);
}
