package com.eventorback.post.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.post.domain.dto.response.GetTempPostResponse;
import com.eventorback.post.service.PostService;
import com.eventorback.search.document.dto.reponse.SearchPostsResponse;
import com.eventorback.search.service.ElasticSearchService;
import com.eventorback.user.domain.dto.CurrentUserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/posts")
public class PostController {
	private final PostService postService;
	private final ElasticSearchService elasticSearchService;

	@GetMapping("/search")
	public ApiResponse<Page<SearchPostsResponse>> searchBooks(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestParam String keyword) {
		return ApiResponse.createSuccess(elasticSearchService.searchPosts(pageable, keyword));
	}

	@AuthorizeRole("admin")
	@GetMapping("/all/paging")
	public ApiResponse<Page<GetPostSimpleResponse>> getPosts(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(postService.getPosts(pageable));
	}

	@GetMapping("/event/hot")
	public ApiResponse<List<GetMainPostResponse>> getHotEventPosts() {
		return ApiResponse.createSuccess(postService.getHotEventPosts());
	}

	@GetMapping("/event/latest")
	public ApiResponse<List<GetMainPostResponse>> getLatestEventPosts() {
		return ApiResponse.createSuccess(postService.getLatestEventPosts());
	}

	@GetMapping("/event/deadline")
	public ApiResponse<List<GetMainPostResponse>> getDeadlineEventPosts() {
		return ApiResponse.createSuccess(postService.getDeadlineEventPosts());
	}

	@GetMapping("/event/recommendation")
	public ApiResponse<List<GetRecommendPostResponse>> getRecommendationEventPosts() {
		return ApiResponse.createSuccess(postService.getRecommendationEventPosts());
	}

	@GetMapping("/event/trending")
	ApiResponse<List<GetRecommendPostResponse>> getTrendingEventPosts() {
		return ApiResponse.createSuccess(postService.getTrendingEventPosts());
	}

	@GetMapping("/hot")
	public ApiResponse<List<GetMainPostResponse>> getHotPostsByCategoryName(
		@CurrentUser CurrentUserDto currentUser,
		@RequestParam String categoryName) {
		return ApiResponse.createSuccess(postService.getHotPostsByCategoryName(currentUser, categoryName));
	}

	@GetMapping
	public ApiResponse<Page<GetPostsByCategoryNameResponse>> getPostsByCategoryName(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestParam String categoryName) {
		return ApiResponse.createSuccess(postService.getPostsByCategoryName(pageable, categoryName));
	}

	@AuthorizeRole("member")
	@GetMapping("/me/paging")
	public ApiResponse<Page<GetPostSimpleResponse>> getPostsByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ApiResponse.createSuccess(postService.getPostsByUserId(pageable, userId));
	}

	@GetMapping("/{postId}")
	public ApiResponse<GetPostResponse> getPost(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.getPost(currentUser, postId));
	}

	@AuthorizeRole("member")
	@GetMapping("/temp")
	ApiResponse<GetTempPostResponse> getTempPost(@CurrentUserId Long userId) {
		return ApiResponse.createSuccess(postService.getTempPost(userId));
	}

	@AuthorizeRole("member")
	@PostMapping
	public ApiResponse<CreatePostResponse> createPost(@CurrentUserId Long userId,
		@RequestBody CreatePostRequest request, @RequestParam boolean isTemp) {
		return ApiResponse.createSuccess(postService.createPost(userId, request, isTemp), "게시물을 등록 하였습니다.");
	}

	@AuthorizeRole("member")
	@PutMapping("/{postId}")
	public ApiResponse<Void> updatePost(@CurrentUser CurrentUserDto currentUser, @PathVariable Long postId,
		@RequestBody UpdatePostRequest request, @RequestParam boolean isTemp) {
		postService.updatePost(currentUser, postId, request, isTemp);
		return ApiResponse.createSuccess("게시물을 수정 하였습니다.");
	}

	@PutMapping("/{postId}/recommend")
	public ApiResponse<Void> recommendPost(@CurrentUserId Long userId, @PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.recommendPost(userId, postId));
	}

	@PutMapping("/{postId}/disrecommend")
	public ApiResponse<Void> disrecommendPost(@CurrentUserId Long userId, @PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.disrecommendPost(userId, postId));
	}

	@AuthorizeRole("member")
	@DeleteMapping("/{postId}")
	public ApiResponse<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return ApiResponse.createSuccess("게시물을 삭제 하였습니다.");
	}

	@AuthorizeRole("member")
	@GetMapping("/{postId}/isAuthorized")
	public ApiResponse<Boolean> isAuthorizedToEdit(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(postService.isAuthorizedToEdit(currentUser, postId));
	}

	@AuthorizeRole("member")
	@DeleteMapping("/temp")
	ApiResponse<Void> deleteTempPost(@CurrentUserId Long userId) {
		postService.deleteTempPost(userId);
		return ApiResponse.createSuccess();
	}
}
