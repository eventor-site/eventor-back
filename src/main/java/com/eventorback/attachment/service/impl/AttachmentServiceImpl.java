// package com.eventorback.attachment.service.impl;
//
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
//
// import com.eventorback.attachment.domain.dto.request.CreateAttachmentRequest;
// import com.eventorback.attachment.domain.entity.Attachment;
// import com.eventorback.attachment.repository.AttachmentRepository;
// import com.eventorback.attachment.service.AttachmentService;
// import com.eventorback.image.domain.entity.Image;
// import com.eventorback.image.exception.ImageNotFoundException;
// import com.eventorback.image.repository.ImageRepository;
// import com.eventorback.post.domain.entity.Post;
// import com.eventorback.post.exception.PostNotFoundException;
// import com.eventorback.post.repository.PostRepository;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @Transactional
// @RequiredArgsConstructor
// public class AttachmentServiceImpl implements AttachmentService {
// 	private final AttachmentRepository attachmentRepository;
// 	private final PostRepository postRepository;
// 	private final ImageRepository imageRepository;
//
// 	@Override
// 	public void createAttachment(CreateAttachmentRequest request) {
// 		Image image = imageRepository.findById(request.imageId())
// 			.orElseThrow(() -> new ImageNotFoundException(request.imageId()));
// 		Post post = postRepository.findById(request.imageId())
// 			.orElseThrow(() -> new PostNotFoundException(request.postId()));
// 		attachmentRepository.save(Attachment.toEntity(image, post));
// 	}
//
// 	@Override
// 	public void deleteAttachment(Long attachmentId) {
// 		attachmentRepository.deleteById(attachmentId);
// 	}
// }
