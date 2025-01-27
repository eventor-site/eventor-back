package com.eventorback.comment.repository;

import java.util.List;
import java.util.Optional;

import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface CustomCommentRepository {

	List<GetCommentByUserIdResponse> getComments();

	List<GetCommentResponse> getCommentsByPostId(CurrentUserDto currentUser, Long postId);

	List<GetCommentByUserIdResponse> getCommentsByUserId(Long userId);

	Optional<Comment> getComment(Long commentId);

	Long getMaxGroup();

	Long getTotalChildCount(Long group);

	List<Comment> getGreaterGroupOrder(Long group, Long groupOrder);
}
