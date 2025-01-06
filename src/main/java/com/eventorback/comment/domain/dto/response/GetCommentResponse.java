package com.eventorback.comment.domain.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.user.domain.dto.CurrentUserDto;

import lombok.Builder;

@Builder
public record GetCommentResponse(
	Long commentId,
	Long parentCommentId,
	String writer,
	String content,
	Long recommendationCount,
	Long decommendationCount,
	List<GetCommentResponse> childComments,
	LocalDateTime createdAt,
	Boolean isAuthorized) {

	public static GetCommentResponse fromEntity(Comment comment, CurrentUserDto currentUser) {
		Long parentCommentId =
			comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null;

		List<GetCommentResponse> childComments = comment.getChildrenComments() != null ?
			comment.getChildrenComments().stream()
				.map(childComment -> GetCommentResponse.fromEntity(childComment, currentUser)) // 람다식으로 변경
				.toList() : null;

		Boolean isAuthorized =
			currentUser != null && (comment.getUser().getUserId().equals(currentUser.userId()) || currentUser.roles()
				.contains("admin"));

		return GetCommentResponse.builder()
			.commentId(comment.getCommentId())
			.parentCommentId(parentCommentId)
			.writer(comment.getWriter())
			.content(comment.getContent())
			.recommendationCount(comment.getRecommendationCount())
			.decommendationCount(comment.getDecommendationCount())
			.childComments(childComments)
			.createdAt(comment.getCreatedAt())
			.isAuthorized(isAuthorized)
			.build();
	}
}
