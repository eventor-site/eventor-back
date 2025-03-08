package com.eventorback.comment.controller;

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
	public ApiResponse<Page<GetCommentResponse>> getCommentsByPostId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUser CurrentUserDto currentUser,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(commentService.getCommentsByPostId(pageable, currentUser, postId));
	}

	@AuthorizeRole("admin")
	@GetMapping("/users/admin/comments/paging")
	public ApiResponse<Page<GetCommentByUserIdResponse>> getComments(
		@PageableDefault(page = 1, size = 10) Pageable pageable) {
		return ApiResponse.createSuccess(commentService.getComments(pageable));
	}

	@AuthorizeRole("member")
	@GetMapping("/users/me/comments/paging")
	public ApiResponse<Page<GetCommentByUserIdResponse>> getCommentsByUserId(
		@PageableDefault(page = 1, size = 10) Pageable pageable, @CurrentUserId Long userId) {
		return ApiResponse.createSuccess(commentService.getCommentsByUserId(pageable, userId));
	}

	@AuthorizeRole("member")
	@PostMapping("/posts/{postId}/comments")
	public ApiResponse<Void> createComment(@RequestBody CreateCommentRequest request, @PathVariable Long postId,
		@CurrentUserId Long userId) {
		commentService.createComment(request, postId, userId);
		return ApiResponse.createSuccess("댓글이 작성 되었습니다.");
	}

	@GetMapping("/posts/{postId}/comments/{commentId}")
	public ApiResponse<GetCommentPageResponse> getComment(
		@PathVariable Long commentId, @PathVariable Long postId) {
		return ApiResponse.createSuccess(commentService.getComment(postId, commentId));
	}

	@AuthorizeRole("member")
	@PutMapping("/posts/{postId}/comments/{commentId}")
	public ApiResponse<Void> updateComment(@CurrentUser CurrentUserDto currentUser, @PathVariable Long commentId,
		@Valid @RequestBody UpdateCommentRequest request, @PathVariable Long postId) {
		commentService.updateComment(currentUser, commentId, request);
		return ApiResponse.createSuccess("댓글이 수정 되었습니다.");
	}

	@PutMapping("/posts/{postId}/comments/{commentId}/recommend")
	public ApiResponse<Void> recommendComment(@CurrentUserId Long userId, @PathVariable Long commentId,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(commentService.recommendComment(userId, commentId));
	}

	@PutMapping("/posts/{postId}/comments/{commentId}/disrecommend")
	public ApiResponse<Void> disrecommendComment(@CurrentUserId Long userId, @PathVariable Long commentId,
		@PathVariable Long postId) {
		return ApiResponse.createSuccess(commentService.disrecommendComment(userId, commentId));
	}

	@DeleteMapping("/posts/{postId}/comments/{commentId}")
	public ApiResponse<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long postId) {
		commentService.deleteComment(commentId);
		return ApiResponse.createSuccess("댓글을 삭제 하였습니다.");
	}

}
