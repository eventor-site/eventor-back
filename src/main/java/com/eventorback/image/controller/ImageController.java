package com.eventorback.image.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventorback.auth.annotation.CurrentUserId;
import com.eventorback.image.domain.dto.request.DeleteImageRequest;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.service.ImageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/images")
public class ImageController {
	private final ImageService imageService;

	@PostMapping(value = "/upload", consumes = "multipart/form-data")
	public ResponseEntity<List<GetImageResponse>> uploadImage(@RequestParam("file") MultipartFile file,
		@RequestParam String folderName, @RequestParam Long postId,
		@RequestParam boolean isThumbnail, @RequestParam boolean isPasted) {
		return ResponseEntity.status(HttpStatus.OK)
			.body(imageService.upload(file, folderName, postId, isThumbnail, isPasted));
	}

	@DeleteMapping
	public ResponseEntity<List<GetImageResponse>> deleteImage(@RequestBody DeleteImageRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(imageService.deleteImage(request));
	}

	@DeleteMapping("/temp")
	public ResponseEntity<Void> deleteTempImage(@CurrentUserId Long userId) {
		imageService.deleteTempImage(userId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
