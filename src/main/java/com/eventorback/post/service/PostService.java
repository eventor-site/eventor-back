package com.eventorback.post.service;

import java.util.List;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;

public interface PostService {

	List<GetPostSimpleResponse> getPosts();

	List<GetPostSimpleResponse> getPostsByCategoryName(String categoryName);

	GetPostResponse getPost(Long postId);

	CreatePostResponse createPost(Long userId, CreatePostRequest request);

	void updatePost(Long postId, UpdatePostRequest request);

	void deletePost(Long postId);
}
