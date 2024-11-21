package com.eventorback.attachment.domain.dto.request;

import lombok.Builder;

@Builder
public record CreateAttachmentRequest(
	Long imageId,
	Long postId) {
}
