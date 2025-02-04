package com.eventorback.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentPageResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface CommentService {

	Page<GetCommentResponse> getCommentsByPostId(Pageable pageable, CurrentUserDto currentUser, Long postId);

	Page<GetCommentByUserIdResponse> getComments(Pageable pageable);

	Page<GetCommentByUserIdResponse> getCommentsByUserId(Pageable pageable, Long userId);

	GetCommentPageResponse getComment(Long postId, Long commentId);

	void createComment(CreateCommentRequest request, Long postId, Long userId);

	void updateComment(CurrentUserDto currentUser, Long commentId, UpdateCommentRequest request);

	String recommendComment(Long userId, Long commentId);

	String disrecommendComment(Long userId, Long commentId);

	void deleteComment(Long commentId);

}
