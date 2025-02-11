package com.eventorback.search.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.eventorback.image.domain.entity.Image;
import com.eventorback.image.exception.ImageNotFoundException;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.postrecommend.domain.entity.PostRecommend;
import com.eventorback.postview.domain.entity.PostView;
import com.eventorback.search.document.EsPost;
import com.eventorback.search.exception.ElasticSearchNotFoundException;
import com.eventorback.search.repository.ElasticSearchRepository;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class PostSyncToElasticSearch {
	private final ElasticSearchRepository elasticsearchRepository;
	private final ImageRepository imageRepository;

	/**
	 * 엘라스틱서치 게시물 조회수 증가
	 */
	@After("execution(* com.eventorback.postview.repository.PostViewRepository.save(..))")
	public void syncPostToElasticsearchAfterGetPost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof PostView postView) {
			EsPost esPost = elasticsearchRepository.findById(postView.getPost().getPostId())
				.orElseThrow(ElasticSearchNotFoundException::new);
			esPost.increaseViewCount();
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 저장
	 */
	@After("execution(* com.eventorback.post.repository.PostRepository.save(..))")
	public void syncPostToElasticsearchAfterSavePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Post post) {
			EsPost esPost = EsPost.fromEntity(post);
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 업데이트
	 */
	@After("execution(* com.eventorback.post.service.impl.PostServiceImpl.updatePost(..))")
	public void syncPostToElasticsearchAfterUpdatePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[1] instanceof Long postId && args[2] instanceof UpdatePostRequest request) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElseThrow(ElasticSearchNotFoundException::new);
			esPost.update(request);
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 추천/비추천
	 */
	@After("execution(* com.eventorback.postrecommend.repository.PostRecommendRepository.save(..))")
	public void syncPostToElasticsearchAfterRecommendPost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[1] instanceof PostRecommend postRecommend) {
			EsPost esPost = elasticsearchRepository.findById(postRecommend.getPost().getPostId())
				.orElseThrow(ElasticSearchNotFoundException::new);

			if (postRecommend.getRecommendType().getName().equals("추천")) {
				esPost.recommendPost();
			} else {
				esPost.disrecommendPost();
			}

			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 삭제
	 */
	@After("execution(* com.eventorback.post.service.impl.PostServiceImpl.deletePost(..))")
	public void syncPostToElasticsearchAfterDeletePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElseThrow(ElasticSearchNotFoundException::new);
			esPost.updatePostStatus("삭제됨");
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 이미지 업로드 시 썸네일 업데이트
	 */
	@After("execution(* com.eventorback.image.service.impl.ImageServiceImpl.upload(..))")
	public void syncPostToElasticsearchAfterSaveImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && args[2] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElseThrow(ElasticSearchNotFoundException::new);
			Image image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId)
				.orElseThrow(ImageNotFoundException::new);
			esPost.updateImageUrl(image.getUrl());
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 이미지 삭제 시 썸네일 업데이트
	 */
	@After("execution(* com.eventorback.image.service.impl.ImageServiceImpl.deleteImage(..))")
	public void syncPostToElasticsearchAfterDeleteImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && args[0] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElseThrow(ElasticSearchNotFoundException::new);
			Image image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId).orElse(null);
			if (image != null) {
				esPost.updateImageUrl(image.getUrl());
			} else {
				esPost.updateImageUrl(null);
			}
			elasticsearchRepository.save(esPost);
		}
	}

}

