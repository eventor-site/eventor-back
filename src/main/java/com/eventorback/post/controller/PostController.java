package com.eventorback.post.controller;

import java.util.List;

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

	@GetMapping("/all")
	public ResponseEntity<List<GetPostSimpleResponse>> getPosts() {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPosts());
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

	@GetMapping
	public ResponseEntity<GetPostsByCategoryNameResponse> getPostsByCategoryName(
		@CurrentUser CurrentUserDto currentUser,
		@RequestParam String categoryName) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByCategoryName(currentUser, categoryName));
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
	public ResponseEntity<Void> updatePost(@CurrentUserId Long userId, @PathVariable Long postId,
		@RequestBody UpdatePostRequest request) {
		postService.updatePost(userId, postId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
