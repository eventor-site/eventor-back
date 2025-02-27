package com.eventorback.comment.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.auth.annotation.CurrentUser;
import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentPageResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.service.CommentService;
import com.eventorback.user.domain.dto.CurrentUserDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class CommentController {
	private final CommentService commentService;

	@GetMapping("/posts/{postId}/comments/paging")
	public ResponseEntity<Page<GetCommentResponse>> getCommentsByPostId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(commentService.getCommentsByPostId(pageable, currentUser, postId));
	}

	@AuthorizeRole("admin")
	@GetMapping("/users/admin/comments/paging")
	public ResponseEntity<Page<GetCommentByUserIdResponse>> getComments(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getComments(pageable));
	}

	@AuthorizeRole("member")
	@GetMapping("/users/me/comments/paging")
	public ResponseEntity<Page<GetCommentByUserIdResponse>> getCommentsByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByUserId(pageable, userId));
	}

	@AuthorizeRole("member")
	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<Void> createComment(@RequestBody CreateCommentRequest request, @PathVariable Long postId,
		@CurrentUserId Long userId) {
		commentService.createComment(request, postId, userId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@GetMapping("/posts/{postId}/comments/{commentId}")
	public ResponseEntity<GetCommentPageResponse> getComment(
		@PathVariable Long commentId, @PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.getComment(postId, commentId));
	}

	@AuthorizeRole("member")
	@PutMapping("/posts/{postId}/comments/{commentId}")
	public ResponseEntity<Void> updateComment(@CurrentUser CurrentUserDto currentUser, @PathVariable Long commentId,
		@Valid @RequestBody UpdateCommentRequest request, @PathVariable Long postId) {
		commentService.updateComment(currentUser, commentId, request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping("/posts/{postId}/comments/{commentId}/recommend")
	public ResponseEntity<String> recommendComment(@CurrentUserId Long userId, @PathVariable Long commentId,
		@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.recommendComment(userId, commentId));
	}

	@PutMapping("/posts/{postId}/comments/{commentId}/disrecommend")
	public ResponseEntity<String> disrecommendComment(@CurrentUserId Long userId, @PathVariable Long commentId,
		@PathVariable Long postId) {
		return ResponseEntity.status(HttpStatus.OK).body(commentService.disrecommendComment(userId, commentId));
	}

	@DeleteMapping("/posts/{postId}/comments/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long postId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
