package com.eventorback.global.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PutMapping("/search/sync")
	ResponseEntity<ApiResponse<Void>> syncPostToElasticsearch() {
		log.info("게시물 전체 조회 시작");
		// 시작 시간 기록
		Instant startTime = Instant.now();

		List<Post> posts = postRepository.findAll();
		log.info("게시물 전체 조회 끝");

		// 종료 시간 기록
		Instant endTime = Instant.now();
		long durationInSeconds = Duration.between(startTime, endTime).getSeconds();
		log.info("게시물 조회 완료. 총 소요 시간: {} 초", durationInSeconds);

		startTime = Instant.now();
		for (Post post : posts) {
			Long postId = post.getPostId();

			log.info("작업 postId: {}", postId);
			// 이벤트 게시물인 경우

			Image image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true).orElse(null);
			EsPost esPost = EsPost.fromEntity(post, image);
			elasticsearchRepository.save(esPost);
		}

		endTime = Instant.now();
		durationInSeconds = Duration.between(startTime, endTime).getSeconds();
		log.info("게시물 Elasticsearch 동기화 완료. 총 소요 시간: {} 초", durationInSeconds);

		return ApiResponse.createSuccess();
	}

	@Transactional
	@PutMapping("/images/sync")
	public ResponseEntity<ApiResponse<List<GetImageResponse>>> syncImages() {

		log.info("이미지 전체 조회 시작");
		// 시작 시간 기록
		Instant startTime = Instant.now();

		List<Image> images = imageRepository.findAll();
		log.info("이미지 전체 조회 끝");

		// 종료 시간 기록
		Instant endTime = Instant.now();
		long durationInSeconds = Duration.between(startTime, endTime).getSeconds();
		log.info("이미지 조회 완료. 총 소요 시간: {} 초", durationInSeconds);

		startTime = Instant.now();
		for (Image image : images) {
			image.updateType(imageService.determineFileType(image.getExtension()));

			log.info("작업 imageId: {}", image.getImageId());

		}

		endTime = Instant.now();
		durationInSeconds = Duration.between(startTime, endTime).getSeconds();
		log.info("이미지 확장자 값 추가 완료. 총 소요 시간: {} 초", durationInSeconds);

		return ApiResponse.createSuccess();
	}

	@DeleteMapping("/posts/cleanup")
	public void getSoftDeletedPosts() {

		Instant start = Instant.now();  // 시작 시간 기록

		postService.getSoftDeletedPosts();

		Instant end = Instant.now();  // 종료 시간 기록
		long durationSeconds = Duration.between(start, end).toSeconds();  // 걸린 시간 계산 (초 단위)

		log.info("SoftDeleted 게시물 삭제 완료, 걸린 시간: {}초", durationSeconds);
	}

}
