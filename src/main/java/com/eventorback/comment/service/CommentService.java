package com.eventorback.comment.service;

import java.util.List;

import com.eventorback.comment.domain.dto.request.CreateCommentRequest;
import com.eventorback.comment.domain.dto.request.UpdateCommentRequest;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;

public interface CommentService {

	List<GetCommentResponse> getCommentsByPostId(Long postId);

	void createComment(CreateCommentRequest request, Long postId, Long userId);

	void updateComment(Long commentId, UpdateCommentRequest request);

	String recommendComment(Long userId, Long commentId);

	String disrecommendComment(Long userId, Long commentId);

	void deleteComment(Long commentId);

}
