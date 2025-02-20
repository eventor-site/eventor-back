package com.eventorback.search.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
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
import com.eventorback.search.repository.ElasticSearchRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class PostSyncToElasticSearch {
	private final ElasticSearchRepository elasticsearchRepository;
	private final ImageRepository imageRepository;

	/**
	 * 엘라스틱서치 게시물 조회수 증가
	 */
	@AfterReturning("execution(* com.eventorback.postview.repository.PostViewRepository.save(..))")
	public void syncPostToElasticsearchAfterReturningGetPost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof PostView postView) {
			EsPost esPost = elasticsearchRepository.findById(postView.getPost().getPostId())
				.orElse(null);

			if (esPost != null) {
				esPost.increaseViewCount();
				elasticsearchRepository.save(esPost);
			} else {
				log.warn("엘라스틱서치 NOT FOUND 발생");
			}

		}
	}

	/**
	 * 엘라스틱서치 게시물 저장
	 */
	@AfterReturning("execution(* com.eventorback.post.repository.PostRepository.save(..))")
	public void syncPostToElasticsearchAfterReturningSavePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Post post) {
			EsPost esPost = EsPost.fromEntity(post);
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 업데이트
	 */
	@AfterReturning("execution(* com.eventorback.post.service.impl.PostServiceImpl.updatePost(..))")
	public void syncPostToElasticsearchAfterReturningUpdatePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[1] instanceof Long postId && args[2] instanceof UpdatePostRequest request) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {
				esPost.update(request);
				elasticsearchRepository.save(esPost);
			} else {
				log.warn("엘라스틱서치 NOT FOUND 발생");
			}

		}
	}

	/**
	 * 엘라스틱서치 게시물 추천/비추천
	 */
	@AfterReturning("execution(* com.eventorback.postrecommend.repository.PostRecommendRepository.save(..))")
	public void syncPostToElasticsearchAfterReturningRecommendPost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof PostRecommend postRecommend) {
			EsPost esPost = elasticsearchRepository.findById(postRecommend.getPost().getPostId())
				.orElse(null);

			if (esPost != null) {
				if (postRecommend.getRecommendType().getName().equals("추천")) {
					esPost.recommendPost();
				} else {
					esPost.disrecommendPost();
				}

				elasticsearchRepository.save(esPost);
			} else {
				log.warn("엘라스틱서치 NOT FOUND 발생");
			}

		}
	}

	/**
	 * 엘라스틱서치 게시물 삭제
	 */
	@AfterReturning("execution(* com.eventorback.post.service.impl.PostServiceImpl.deletePost(..))")
	public void syncPostToElasticsearchAfterReturningDeletePost(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		if (args.length > 0 && args[0] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {
				esPost.updatePostStatus("삭제됨");
				elasticsearchRepository.save(esPost);
			} else {
				log.warn("엘라스틱서치 NOT FOUND 발생");
			}

		}
	}

	/**
	 * 이벤트 게시물 썸네일 업로드 시 엘라스틱서치 동기화
	 */
	@AfterReturning("execution(* com.eventorback.image.service.impl.ImageServiceImpl.uploadThumbnail(..))")
	public void syncPostToElasticsearchAfterReturningUploadThumbnail(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && args[2] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {
				Image image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true)
					.orElseThrow(ImageNotFoundException::new);
				esPost.updateImageUrl(image.getUrl());
				elasticsearchRepository.save(esPost);

			} else {
				log.warn("엘라스틱서치 NOT FOUND 발생");
			}

		}
	}

	/**
	 * 공지, 핫딜, 자유, 맛집 게시물 이미지 업로드 시 썸네일 엘라스틱서치 업데이트
	 */
	@AfterReturning("execution(* com.eventorback.image.service.impl.ImageServiceImpl.upload(..))")
	public void syncPostToElasticsearchAfterReturningSaveImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && args[2] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {
				String categoryName = esPost.getCategoryName();
				if (categoryName.equals("공지") || categoryName.equals("핫딜") || categoryName.equals("자유")
					|| categoryName.equals("맛집")) {
					Image image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId)
						.orElseThrow(ImageNotFoundException::new);
					esPost.updateImageUrl(image.getUrl());
					elasticsearchRepository.save(esPost);
				}

			} else {
				log.warn("엘라스틱서치 NOT FOUND 발생");
			}

		}
	}

	/**
	 * 공지, 핫딜, 자유, 맛집 게시물 이미지 삭제 시 엘라스틱서치 썸네일 업데이트
	 */
	@AfterReturning("execution(* com.eventorback.image.service.impl.ImageServiceImpl.deleteImage(..))")
	public void syncPostToElasticsearchAfterReturningDeleteImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && args[0] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {
				String categoryName = esPost.getCategoryName();
				if (categoryName.equals("공지") || categoryName.equals("핫딜") || categoryName.equals("자유")
					|| categoryName.equals("맛집")) {

					Image image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId).orElse(null);

					if (image != null) {
						esPost.updateImageUrl(image.getUrl());
					} else {
						esPost.updateImageUrl(null);
					}

					elasticsearchRepository.save(esPost);
				} else {
					log.warn("엘라스틱서치 NOT FOUND 발생");
				}
			}
		}
	}
}

