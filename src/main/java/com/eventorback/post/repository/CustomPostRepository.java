package com.eventorback.post.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.response.GetEventPostCountByAdminResponse;
import com.eventorback.post.domain.dto.response.GetMainHotPostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.post.domain.dto.response.GetTempPostResponse;
import com.eventorback.post.domain.entity.Post;

public interface CustomPostRepository {

	Page<GetPostSimpleResponse> getPosts(Pageable pageable);

	Page<GetPostSimpleResponse> monitorPosts(Pageable pageable);

	Page<GetPostSimpleResponse> getPostsByUserId(Pageable pageable, Long userId);

	List<GetMainHotPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetMainPostResponse> getDeadlineEventPosts();

	List<GetRecommendPostResponse> getRecommendationEventPosts();

	List<GetRecommendPostResponse> getTrendingEventPosts();

	List<GetMainPostResponse> getCommunityPosts();

	List<GetMainPostResponse> getHotEventPostsByCategoryName(List<Long> categoryIds);

	List<GetMainPostResponse> getHotPostsByCategoryName(List<Long> categoryIds);

	Page<GetPostsByCategoryNameResponse> getPostsByEventCategory(Pageable pageable, List<Long> categoryIds,
		String eventStatusName, String endType);

	Page<GetPostsByCategoryNameResponse> getPostsByCategoryName(Pageable pageable, List<Long> categoryIds);

	Optional<Post> getPost(Long postId);

	Optional<GetTempPostResponse> getTempPost(Long userId);

	List<GetEventPostCountByAdminResponse> getEventPostCountByAdmin(LocalDateTime startTime, LocalDateTime endTime);

	List<Long> getEventPostsByTitleContainKeyword(String keyword);
}
