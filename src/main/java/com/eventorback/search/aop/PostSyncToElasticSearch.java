package com.eventorback.search.aop;

import java.util.List;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.event.repository.EventRepository;
import com.eventorback.image.domain.dto.request.DeleteImageRequest;
import com.eventorback.image.domain.entity.Image;
import com.eventorback.image.exception.ImageNotFoundException;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
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
	private final CategoryRepository categoryRepository;
	private final EventRepository eventRepository;

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

		if (args.length > 0 && !(boolean)args[2] && args[1] instanceof CreatePostRequest request
			&& result instanceof CreatePostResponse response) {
			Post post = postRepository.findById(response.postId())
				.orElseThrow(PostNotFoundException::new);

			if (post.getEvent() != null) {

			}

			if (post.getHotDeal() != null) {

			}

			EsPost esPost = EsPost.fromEntity(post);
			if (post.getCategory().getName().equals("핫딜")) {
				esPost.updateProductName(request.productName());
			}
			elasticsearchRepository.save(esPost);
		}
	}

	/**
	 * 엘라스틱서치 게시물 업데이트
	 */
	@AfterReturning(value = "execution(* com.eventorback.post.service.impl.PostServiceImpl.updatePost(..))", returning = "result")
	public void syncPostToElasticsearchAfterReturningUpdatePost(JoinPoint joinPoint, Object result) {
		Object[] args = joinPoint.getArgs();
		List<String> eventCategoryNames = categoryRepository.getCategoryNames("이벤트");

		if (args.length > 0 && !(boolean)args[3] && args[2] instanceof UpdatePostRequest request
			&& result instanceof Post post) {
			Long postId = post.getPostId();
			String categoryName = post.getCategory().getName();
			Image image;

			// 이벤트 게시물인 경우
			if (eventCategoryNames.contains(categoryName)) {
				image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true).orElse(null);
			} else {
				image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId)
					.orElse(null);
			}
			EsPost esPost = EsPost.fromEntity(post, image);

			if (categoryName.equals("핫딜")) {
				esPost.updateProductName(request.productName());
			}

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
	 * 공지, 핫딜, 자유, 맛집 게시물 이미지 업로드 시 썸네일 엘라스틱서치 업데이트
	 */
	@AfterReturning("execution(* com.eventorback.image.service.impl.ImageServiceImpl.upload(..))")
	public void syncPostToElasticsearchAfterReturningSaveImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		List<String> eventCategoryNames = categoryRepository.getCategoryNames("이벤트");

		if (args.length > 0 && args[2] instanceof Long postId) {
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {
				String categoryName = esPost.getCategoryName();

				// 이벤트 게시물인 경우
				if (eventCategoryNames.contains(categoryName)) {
					Image image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true).orElse(null);

					if (image != null) {
						esPost.updateImageUrl(image.getUrl());
					} else {
						esPost.updateImageUrl(null);
					}
				} else {
					Image image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId)
						.orElseThrow(ImageNotFoundException::new);
					esPost.updateImageUrl(image.getUrl());
				}

				elasticsearchRepository.save(esPost);

			}

		}
	}

	/**
	 * 공지, 핫딜, 자유, 맛집 게시물 이미지 삭제 시 엘라스틱서치 썸네일 업데이트
	 */
	@AfterReturning("execution(* com.eventorback.image.service.impl.ImageServiceImpl.deleteImage(..))")
	public void syncPostToElasticsearchAfterReturningDeleteImage(JoinPoint joinPoint) {
		Object[] args = joinPoint.getArgs();
		List<String> eventCategoryNames = categoryRepository.getCategoryNames("이벤트");

		if (args.length > 0 && args[0] instanceof DeleteImageRequest request) {
			Long postId = request.postId();
			EsPost esPost = elasticsearchRepository.findById(postId).orElse(null);

			if (esPost != null) {
				String categoryName = esPost.getCategoryName();

				// 이벤트 게시물인 경우
				Image image;
				if (eventCategoryNames.contains(categoryName)) {
					image = imageRepository.findByPostPostIdAndIsThumbnail(postId, true).orElse(null);

				} else {
					image = imageRepository.findTopByPostPostIdOrderByImageIdAsc(postId).orElse(null);
				}

				if (image != null) {
					esPost.updateImageUrl(image.getUrl());
				} else {
					esPost.updateImageUrl(null);
				}

				elasticsearchRepository.save(esPost);
			}
		}
	}
}

