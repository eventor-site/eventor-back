package com.eventorback.post.repository;

import java.util.List;
import java.util.Optional;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface CustomPostRepository {

	List<GetPostSimpleResponse> getPosts();

	List<GetPostSimpleResponse> getPostsByUserId(Long userId);

	List<GetMainPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetMainPostResponse> getRecommendationEventPosts();

	List<GetMainPostResponse> getHotPostsByCategoryName(List<Long> categoryIds);

	List<GetPostSimpleResponse> getPostsByCategoryName(CurrentUserDto currentUser, List<Long> categoryIds);

	Optional<Post> getPost(Long postId);
}
