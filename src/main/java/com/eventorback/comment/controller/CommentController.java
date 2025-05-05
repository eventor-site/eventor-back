package com.eventorback.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class CommentController {
	private final CommentService commentService;

	@GetMapping("/posts/{postId}/comments/paging")
	public ResponseEntity<ApiResponse<Page<GetCommentResponse>>> getCommentsByPostId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(commentService.getCommentsByPostId(pageable, currentUser, postId));
	}

	@AuthorizeRole("admin")
	@GetMapping("/users/admin/comments/paging")
	public ResponseEntity<ApiResponse<Page<GetCommentByUserIdResponse>>> getComments(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(commentService.getComments(pageable));
	}

	@AuthorizeRole("member")
	@GetMapping("/users/me/comments/paging")
	public ResponseEntity<ApiResponse<Page<GetCommentByUserIdResponse>>> getCommentsByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ApiResponse.createSuccess(commentService.getCommentsByUserId(pageable, userId));
	}

	@AuthorizeRole("member")
	@PostMapping("/posts/{postId}/comments")
	public ResponseEntity<ApiResponse<Void>> createComment(@CurrentUser CurrentUserDto currentUser,
		@RequestBody CreateCommentRequest request, @PathVariable Long postId) {
		commentService.createComment(currentUser, request, postId);
		return ApiResponse.createSuccess("댓글이 작성 되었습니다. 포인트 +5");
	}

	@GetMapping("/posts/{postId}/comments/{commentId}")
	public ResponseEntity<ApiResponse<GetCommentPageResponse>> getComment(
		@PathVariable Long commentId, @PathVariable Long postId) {
		return ApiResponse.createSuccess(commentService.getComment(postId, commentId));
	}

	@AuthorizeRole("member")
	@PutMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<Void>> updateComment(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long commentId, @Valid @RequestBody UpdateCommentRequest request) {
		commentService.updateComment(currentUser, commentId, request);
		return ApiResponse.createSuccess("댓글이 수정 되었습니다.");
	}

	@PutMapping("/comments/{commentId}/recommend")
	public ResponseEntity<ApiResponse<Void>> recommendComment(@CurrentUserId Long userId,
		@PathVariable Long commentId) {
		return ApiResponse.createSuccess(commentService.recommendComment(userId, commentId));
	}

	@PutMapping("/comments/{commentId}/disrecommend")
	public ResponseEntity<ApiResponse<Void>> disrecommendComment(@CurrentUserId Long userId,
		@PathVariable Long commentId) {
		return ApiResponse.createSuccess(commentService.disrecommendComment(userId, commentId));
	}

	@AuthorizeRole("member")
	@DeleteMapping("/comments/{commentId}")
	public ResponseEntity<ApiResponse<Void>> deleteComment(@CurrentUser CurrentUserDto currentUser,
		@PathVariable Long commentId) {
		commentService.deleteComment(currentUser, commentId);
		return ApiResponse.createSuccess("댓글을 삭제 하였습니다. 포인트 -5");
	}

}
