package com.eventorback.post.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetEventPostCountByAdminResponse;
import com.eventorback.post.domain.dto.response.GetFixedPostResponse;
import com.eventorback.post.domain.dto.response.GetMainHotPostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.post.domain.dto.response.GetTempPostResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface PostService {

	void evictMainPageCache();

	Page<GetPostSimpleResponse> getPosts(Pageable pageable);

	Page<GetPostSimpleResponse> getSpecialNoticeEventPosts(Pageable pageable);

	List<GetMainHotPostResponse> getHotEventPosts();

	List<GetMainPostResponse> getLatestEventPosts();

	List<GetMainPostResponse> getDeadlineEventPosts();

	List<GetRecommendPostResponse> getRecommendationEventPosts();

	List<GetRecommendPostResponse> getTrendingEventPosts();

	List<GetMainPostResponse> getHotDealPosts();

	List<GetMainPostResponse> getCommunityPosts();

	List<GetFixedPostResponse> getFixedPostsByCategoryName(String categoryName);

	List<GetMainPostResponse> getHotPostsByCategoryName(CurrentUserDto currentUser, String categoryName);

	Page<GetPostsByCategoryNameResponse> getPostsByCategoryName(Pageable pageable, String categoryName,
		String eventStatusName, String endType);

	Page<GetPostSimpleResponse> getPostsByUserId(Pageable pageable, Long userId);

	GetPostResponse getPost(CurrentUserDto currentUser, String uuid, Long postId);

	GetTempPostResponse getTempPost(Long userId);

	CreatePostResponse createPost(CurrentUserDto currentUserDto, CreatePostRequest request, boolean isTemp);

	Post updatePost(CurrentUserDto currentUser, Long postId, UpdatePostRequest request, boolean isTemp);

	void finishEventPost(Long postId);

	String recommendPost(Long userId, Long postId);

	String disrecommendPost(Long userId, Long postId);

	Post deletePost(CurrentUserDto currentUser, Long postId);

	void deleteExpiredPosts();

	void deleteSoftDeletedPosts();

	Boolean isAuthorizedToEdit(CurrentUserDto currentUser, Long postId);

	void updatePostIsFixed(Long postId, Boolean isFixed);

	void deleteTempPost(Long userId);

	List<GetEventPostCountByAdminResponse> getEventPostCountByAdmin(LocalDateTime startTime, LocalDateTime endTime);

	Integer deleteEventPostsByTitleContainKeyword(CurrentUserDto currentUser, String keyword);

	void createSitemap();
}
