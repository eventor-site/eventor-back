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
	String gradeName,
	Boolean isAuthorized,
	Boolean isDeleted) {

	public static GetCommentResponse fromEntity(Comment comment, CurrentUserDto currentUser) {
		Long parentCommentId =
			comment.getParentComment() != null ? comment.getParentComment().getCommentId() : null;

		List<GetCommentResponse> childComments = comment.getChildrenComments() != null ?
			comment.getChildrenComments().stream()
				.map(childComment -> GetCommentResponse.fromEntity(childComment, currentUser))
				.toList() : null;

		String content = comment.getStatus().getName().equals("삭제됨") ? "삭제된 댓글입니다." : comment.getContent();

		Boolean isAuthorized =
			currentUser != null && (comment.getUser().getUserId().equals(currentUser.userId()) || currentUser.roles()
				.contains("admin"));

		return GetCommentResponse.builder()
			.commentId(comment.getCommentId())
			.parentCommentId(parentCommentId)
			.writer(comment.getWriter())
			.content(content)
			.recommendationCount(comment.getRecommendationCount())
			.decommendationCount(comment.getDecommendationCount())
			.childComments(childComments)
			.createdAt(comment.getCreatedAt())
			.gradeName(comment.getUser().getGrade().getName())
			.isAuthorized(isAuthorized)
			.isDeleted(comment.getStatus().getName().equals("삭제됨"))
			.build();
	}
}
