package com.eventorback.global.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eventorback.global.annotation.TimedExecution;
import com.eventorback.global.dto.ApiResponse;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.domain.entity.Image;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.image.service.ImageService;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.post.service.PostService;
import com.eventorback.search.document.EsPost;
import com.eventorback.search.repository.ElasticSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/back")
public class GlobalController {
	private final PostRepository postRepository;
	private final ImageRepository imageRepository;
	private final ElasticSearchRepository elasticsearchRepository;
	private final ImageService imageService;
	private final PostService postService;

	@TimedExecution("게시물 ElasticSearch 동기화")
	@PutMapping("/search/sync")
	ResponseEntity<ApiResponse<Void>> syncPostToElasticsearch() {
		List<Post> posts = postRepository.findAll();

		for (Post post : posts) {
			Long postId = post.getPostId();

			log.info("작업 postId: {}", postId);

			Image image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true).orElse(null);
			EsPost esPost = EsPost.fromEntity(post, image);
			elasticsearchRepository.save(esPost);
		}

		return ApiResponse.createSuccess("게시물 ElasticSearch 동기화 완료");
	}

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

	@TimedExecution("SoftDeleted 게시물 삭제")
	@DeleteMapping("/posts/cleanup")
	public ResponseEntity<ApiResponse<List<GetImageResponse>>> getSoftDeletedPosts() {
		postService.getSoftDeletedPosts();

		return ApiResponse.createSuccess("SoftDeleted 게시물 삭제 완료");
	}

	@TimedExecution("Sitemap.xml 파일 생성")
	@PostMapping("/posts/sitemap")
	public ResponseEntity<ApiResponse<Void>> createSitemap() {
		postService.createSitemap();

		return ApiResponse.createSuccess("Sitemap.xml 파일 생성 완료");
	}

}
