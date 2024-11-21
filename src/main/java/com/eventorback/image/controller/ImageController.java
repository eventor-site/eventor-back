package com.eventorback.image.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eventorback.image.service.impl.ImageServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/back/images")
public class ImageController {
	private final ImageServiceImpl uploadService;

	@PostMapping("/upload")
	public ResponseEntity<Void> uploadImage(@RequestParam("files") List<MultipartFile> files,
		@RequestParam String folderName, @RequestParam Long postId) {
		uploadService.upload(files, folderName, postId);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
