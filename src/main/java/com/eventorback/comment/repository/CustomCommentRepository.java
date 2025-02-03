package com.eventorback.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.comment.domain.dto.response.GetCommentByUserIdResponse;
import com.eventorback.comment.domain.dto.response.GetCommentResponse;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface CustomCommentRepository {

	Page<GetCommentByUserIdResponse> getComments(Pageable pageable);

	Page<GetCommentResponse> getCommentsByPostId(Pageable pageable, CurrentUserDto currentUser, Long postId);

	Page<GetCommentByUserIdResponse> getCommentsByUserId(Pageable pageable, Long userId);

	Optional<Comment> getComment(Long commentId);

	Long getMaxGroup();

	Long getTotalChildCount(Long group);

	List<Comment> getGreaterGroupOrder(Long group, Long groupOrder);
}
