package com.eventorback.post.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
import com.eventorback.post.service.PostService;
import com.eventorback.user.domain.dto.CurrentUserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/posts")
public class PostController {
	private final PostService postService;

	@GetMapping("/all/paging")
	public ResponseEntity<Page<GetPostSimpleResponse>> getPosts(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPosts(pageable));
	}

	@GetMapping("/event/hot")
	public ResponseEntity<List<GetMainPostResponse>> getHotEventPosts() {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getHotEventPosts());
	}

	@GetMapping("/event/latest")
	public ResponseEntity<List<GetMainPostResponse>> getLatestEventPosts() {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getLatestEventPosts());
	}

	@GetMapping("/event/recommendation")
	public ResponseEntity<List<GetMainPostResponse>> getRecommendationEventPosts() {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getRecommendationEventPosts());
	}

	@GetMapping("/hot")
	public ResponseEntity<List<GetMainPostResponse>> getHotPostsByCategoryName(
		@CurrentUser CurrentUserDto currentUser,
		@RequestParam String categoryName) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(postService.getHotPostsByCategoryName(currentUser, categoryName));
	}

	@GetMapping
	public ApiResponse<Page<GetPostsByCategoryNameResponse>> getPostsByCategoryName(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @RequestParam String categoryName) {
		return ApiResponse.createSuccess(postService.getPostsByCategoryName(pageable, categoryName));
	}

	@GetMapping("/me/paging")
	public ResponseEntity<Page<GetPostSimpleResponse>> getPostsByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByUserId(pageable, userId));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<GetPostResponse> getPost(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(currentUser, postId));
	}

	@PostMapping
	public ResponseEntity<CreatePostResponse> createPost(@CurrentUserId Long userId,
		@RequestBody CreatePostRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(userId, request));
	}

	@PutMapping("/{postId}")
	public ResponseEntity<Void> updatePost(@CurrentUser CurrentUserDto currentUser, @PathVariable Long postId,
		@RequestBody UpdatePostRequest request) {
		postService.updatePost(currentUser, postId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/{postId}/recommend")
	public ResponseEntity<String> recommendPost(@CurrentUserId Long userId, @PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.recommendPost(userId, postId));
	}

	@PutMapping("/{postId}/disrecommend")
	public ResponseEntity<String> disrecommendPost(@CurrentUserId Long userId, @PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.disrecommendPost(userId, postId));
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
