package com.eventorback.comment.service;

import java.util.List;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface CommentService {

	List<GetCommentResponse> getCommentsByPostId(CurrentUserDto currentUser, Long postId);

	List<GetCommentByUserIdResponse> getCommentsByUserId(Long userId);

	void createComment(CreateCommentRequest request, Long postId, Long userId);

	void updateComment(CurrentUserDto currentUser, Long commentId, UpdateCommentRequest request);

	String recommendComment(Long userId, Long commentId);

	String disrecommendComment(Long userId, Long commentId);

	void deleteComment(Long commentId);

}
