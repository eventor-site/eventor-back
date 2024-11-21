// package com.eventorback.attachment.controller;
//
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import com.eventorback.attachment.domain.dto.request.CreateAttachmentRequest;
// import com.eventorback.attachment.service.AttachmentService;
//
// import lombok.RequiredArgsConstructor;
//
// @RestController
// @RequiredArgsConstructor
// @RequestMapping("/back/attachments")
// public class AttachmentController {
// 	private final AttachmentService attachmentService;
//
// 	@PostMapping
// 	public ResponseEntity<Void> createAttachment(
// 		@RequestBody CreateAttachmentRequest request) {
// 		attachmentService.createAttachment(request);
// 		return ResponseEntity.status(HttpStatus.CREATED).build();
// 	}
//
// 	@DeleteMapping("/{postId}")
// 	public ResponseEntity<Void> deleteAttachmentByPostId(@PathVariable Long postId) {
// 		attachmentService.deleteAttachment(postId);
// 		return ResponseEntity.status(HttpStatus.OK).build();
// 	}
//
// }
