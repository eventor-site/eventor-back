package com.eventorback.post.repository;

import java.util.List;
import java.util.Optional;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface CustomPostRepository {

	List<GetPostSimpleResponse> getPosts();

	List<GetMainPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetMainPostResponse> getRecommendationEventPosts();

	List<GetPostSimpleResponse> getPostsByCategoryName(CurrentUserDto currentUser, String categoryName);

	Optional<GetPostResponse> getPost(Long postId);
}
