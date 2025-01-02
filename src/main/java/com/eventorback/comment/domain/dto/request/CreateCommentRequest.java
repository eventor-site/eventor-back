package com.eventorback.comment.domain.dto.request;

public record CreateCommentRequest(
	Long parentCommentId,
	String content) {
}
