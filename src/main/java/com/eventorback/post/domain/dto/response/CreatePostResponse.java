package com.eventorback.post.domain.dto.response;

import com.eventorback.post.domain.entity.Post;

import lombok.Builder;

@Builder
public record CreatePostResponse(
	Long postId) {
	public static CreatePostResponse fromEntity(Post post) {
		return CreatePostResponse.builder().postId(post.getPostId()).build();
	}
}
