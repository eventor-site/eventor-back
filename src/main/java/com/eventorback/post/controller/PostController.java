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

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.service.PostService;

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

	@GetMapping
	public ResponseEntity<List<GetPostSimpleResponse>> getPostsByCategoryName(@RequestParam String categoryName) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByCategoryName(categoryName));
	}

	@GetMapping("/{postId}")
	public ResponseEntity<GetPostResponse> getPost(@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
	}

	@PostMapping
	public ResponseEntity<CreatePostResponse> createPost(@CurrentUserId Long userId,
		@RequestBody CreatePostRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(userId, request));
	}

	@PutMapping("/{postId}")
	public ResponseEntity<Void> updatePost(@PathVariable Long postId,
		@RequestBody UpdatePostRequest request) {
		postService.updatePost(postId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
		postService.deletePost(postId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
