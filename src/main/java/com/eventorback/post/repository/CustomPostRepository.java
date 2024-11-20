package com.eventorback.post.repository;

import java.util.List;
import java.util.Optional;

import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;

public interface CustomPostRepository {

	List<GetPostSimpleResponse> getPostsByCategoryName(String categoryName);

	List<GetPostSimpleResponse> getPosts();

	Optional<GetPostResponse> getPost(Long postId);
}
