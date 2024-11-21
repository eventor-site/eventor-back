package com.eventorback.attachment.service;

import com.eventorback.attachment.domain.dto.request.CreateAttachmentRequest;

public interface AttachmentService {

	void createAttachment(CreateAttachmentRequest request);

	void deleteAttachment(Long attachmentId);
}
