package com.eventorback.comment.repository;

import java.util.List;
import java.util.Optional;

import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;

public interface CustomCommentRepository {

	List<GetCommentResponse> getCommentsByPostId(Long postId);

	Optional<Comment> getComment(Long commentId);
}
