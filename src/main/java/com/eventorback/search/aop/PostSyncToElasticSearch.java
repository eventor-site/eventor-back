package com.eventorback.search.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.eventorback.image.domain.dto.request.DeleteImageRequest;
import com.eventorback.image.domain.entity.Image;
import com.eventorback.image.exception.ImageNotFoundException;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
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
	private final PostRepository postRepository;

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
	@AfterReturning(value = "execution(* com.eventorback.post.service.impl.PostServiceImpl.createPost(..))", returning = "result")
	public void syncPostToElasticsearchAfterReturningCreatePost(JoinPoint joinPoint, Object result) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && !(boolean)args[2] && result instanceof CreatePostResponse(Long postId)) {
			Post post = postRepository.findById(postId)
				.orElseThrow(PostNotFoundException::new);

			EsPost esPost = EsPost.fromEntity(post, null);
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 업데이트
	 */
	@AfterReturning(value = "execution(* com.eventorback.post.service.impl.PostServiceImpl.updatePost(..))", returning = "result")
	public void syncPostToElasticsearchAfterReturningUpdatePost(JoinPoint joinPoint, Object result) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && !(boolean)args[3] && result instanceof Post post) {
			Long postId = post.getPostId();
			Image image;

			// 이벤트 게시물인 경우
			if (post.getEvent() != null) {
				image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true).orElse(null);
			} else {
				image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId).orElse(null);
			}

			EsPost esPost = EsPost.fromEntity(post, image);
			elasticsearchRepository.save(esPost);
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
	 * 게시물 이미지 업로드 시 썸네일 엘라스틱서치 업데이트
	 */
	@AfterReturning("execution(* com.eventorback.image.service.impl.ImageServiceImpl.upload(..))")
	public void syncPostToElasticsearchAfterReturningSaveImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && args[2] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			Image image = null;
			if (esPost != null) {
				if (esPost.getStartTime() != null) {
					image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true)
						.orElse(null);
				} else if (esPost.getProductName() != null) {
					image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId)
						.orElseThrow(ImageNotFoundException::new);
				}
				esPost.updateImageUrl(image);
				elasticsearchRepository.save(esPost);

			}

		}
	}

	/**
	 * 이미지 삭제 시 엘라스틱서치 썸네일 업데이트
	 */
	@AfterReturning("execution(* com.eventorback.image.service.impl.ImageServiceImpl.deleteImage(..))")
	public void syncPostToElasticsearchAfterReturningDeleteImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();

		if (args.length > 0 && args[0] instanceof DeleteImageRequest request) {
			Long postId = request.postId();
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {

				// 이벤트 게시물인 경우
				Image image;
				if (esPost.getStartTime() != null) {
					image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true).orElse(null);
				} else {
					image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId).orElse(null);
				}

				esPost.updateImageUrl(image);
				elasticsearchRepository.save(esPost);
			}
		}
	}
}

