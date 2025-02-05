package com.eventorback.post.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface PostService {

	Page<GetPostSimpleResponse> getPosts(Pageable pageable);

	List<GetMainPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetRecommendPostResponse> getRecommendationEventPosts();

	List<GetMainPostResponse> getHotPostsByCategoryName(CurrentUserDto currentUser, String categoryName);

	Page<GetPostsByCategoryNameResponse> getPostsByCategoryName(Pageable pageable, String categoryName);

	Page<GetPostSimpleResponse> getPostsByUserId(Pageable pageable, Long userId);

	GetPostResponse getPost(CurrentUserDto currentUser, Long postId);

	CreatePostResponse createPost(Long userId, CreatePostRequest request);

	void updatePost(CurrentUserDto currentUser, Long postId, UpdatePostRequest request);

	String recommendPost(Long userId, Long postId);

	String disrecommendPost(Long userId, Long postId);

	void deletePost(Long postId);
}
