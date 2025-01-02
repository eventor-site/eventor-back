package com.eventorback.comment.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.service.CommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/posts/{postId}/comments")
public class CommentController {
	private final CommentService commentService;

	@GetMapping
	public ResponseEntity<List<GetCommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.CREATED).body(commentService.getCommentsByPostId(postId));
	}

	@PostMapping
	public ResponseEntity<Void> createComment(@RequestBody CreateCommentRequest request, @PathVariable Long postId,
		@CurrentUserId Long userId) {
		commentService.createComment(request, postId, userId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@PutMapping("/{commentId}")
	public ResponseEntity<Void> updateComment(@PathVariable Long commentId,
		@Valid @RequestBody UpdateCommentRequest request, @PathVariable Long postId) {
		commentService.updateComment(commentId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/{commentId}/recommend")
	public ResponseEntity<Void> recommendComment(@PathVariable Long commentId, @PathVariable Long postId,
		@CurrentUserId Long userId) {
		commentService.recommendComment(commentId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/{commentId}/disrecommend")
	public ResponseEntity<Void> disrecommendComment(@PathVariable Long commentId, @PathVariable Long postId,
		@CurrentUserId Long userId) {
		commentService.disrecommendComment(commentId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long postId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
