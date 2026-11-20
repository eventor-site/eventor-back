package com.eventorback.global.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.auth.annotation.AuthorizeRole;
import com.eventorback.global.annotation.TimedExecution;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.domain.entity.Image;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.image.service.ImageService;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.post.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GlobalController {
	private final PostRepository postRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;
	private final PostService postService;

	@AuthorizeRole("admin")
	@TimedExecution("이미지 확장자 값 추가")
	@Transactional
	@PutMapping("/images/sync")
	public ResponseEntity<ApiResponse<List<GetImageResponse>>> syncImages() {
		List<Image> images = imageRepository.findAll();

		for (Image image : images) {
			image.updateType(imageService.determineFileType(image.getExtension()));

			log.info("작업 imageId: {}", image.getImageId());
		}

		return ApiResponse.createSuccess("이미지 확장자 값 추가 완료");
	}

	@AuthorizeRole("admin")
	@TimedExecution("SoftDeleted 게시물 삭제")
	@DeleteMapping("/posts/cleanup")
	public ResponseEntity<ApiResponse<List<GetImageResponse>>> deleteSoftDeletedPosts() {
		postService.deleteSoftDeletedPosts();

		return ApiResponse.createSuccess("SoftDeleted 게시물 삭제 완료");
	}

}
