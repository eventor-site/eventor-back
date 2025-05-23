package com.eventorback.post.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.exception.CategoryNotFoundException;
import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.category.service.CategoryService;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.repository.CommentRepository;
import com.eventorback.event.domain.entity.Event;
import com.eventorback.event.repository.EventRepository;
import com.eventorback.favorite.repository.FavoriteRepository;
import com.eventorback.global.annotation.TimedExecution;
import com.eventorback.hotdeal.domain.entity.HotDeal;
import com.eventorback.hotdeal.repository.HotDealRepository;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.exception.FileSaveException;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.image.service.ImageService;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetEventPostCountByAdminResponse;
import com.eventorback.post.domain.dto.response.GetFixedPostResponse;
import com.eventorback.post.domain.dto.response.GetMainHotPostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
import com.eventorback.post.domain.dto.response.GetSitemapResponse;
import com.eventorback.post.domain.dto.response.GetTempPostResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.post.service.PostService;
import com.eventorback.postrecommend.domain.entity.PostRecommend;
import com.eventorback.postrecommend.repository.PostRecommendRepository;
import com.eventorback.postview.domain.entity.PostView;
import com.eventorback.postview.repository.PostViewRepository;
import com.eventorback.recommendtype.domain.entity.RecommendType;
import com.eventorback.recommendtype.service.RecommendTypeService;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserForbiddenException;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final EventRepository eventRepository;
	private final HotDealRepository hotDealRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final StatusRepository statusRepository;
	private final ImageRepository imageRepository;
	private final PostRecommendRepository postRecommendRepository;
	private final PostViewRepository postViewRepository;
	private final RecommendTypeService recommendTypeService;
	private final FavoriteRepository favoriteRepository;
	private final CommentRepository commentRepository;
	private final CategoryService categoryService;
	private final ApplicationContext applicationContext;
	private final ImageService imageService;

	@Value("${upload.domainUrl}")
	private String domainUrl;

	@Value("${upload.path}")
	private String uploadPath;

	@Override
	@Caching(evict = {
		@CacheEvict(cacheNames = "cache", key = "'hotEventPosts'", cacheManager = "cacheManager"),
		@CacheEvict(cacheNames = "cache", key = "'latestEventPosts'", cacheManager = "cacheManager"),
		@CacheEvict(cacheNames = "cache", key = "'deadlineEventPosts'", cacheManager = "cacheManager"),
		@CacheEvict(cacheNames = "cache", key = "'recommendationEventPosts'", cacheManager = "cacheManager"),
		@CacheEvict(cacheNames = "cache", key = "'trendingEventPosts'", cacheManager = "cacheManager"),
		@CacheEvict(cacheNames = "cache", key = "'hotDealPosts'", cacheManager = "cacheManager"),
		@CacheEvict(cacheNames = "cache", key = "'communityPosts'", cacheManager = "cacheManager")
	})
	public void evictMainPageCache() {
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetPostSimpleResponse> getPosts(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return postRepository.getPosts(PageRequest.of(page, pageSize));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetPostSimpleResponse> getSpecialNoticeEventPosts(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return postRepository.getSpecialNoticeEventPosts(PageRequest.of(page, pageSize));
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'hotEventPosts'", cacheManager = "cacheManager")
	public List<GetMainHotPostResponse> getHotEventPosts() {
		return postRepository.getHotEventPosts();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'latestEventPosts'", cacheManager = "cacheManager")
	public List<GetMainPostResponse> getLatestEventPosts() {
		return postRepository.getLatestEventPosts();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'deadlineEventPosts'", cacheManager = "cacheManager")
	public List<GetMainPostResponse> getDeadlineEventPosts() {
		return postRepository.getDeadlineEventPosts();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'recommendationEventPosts'", cacheManager = "cacheManager")
	public List<GetRecommendPostResponse> getRecommendationEventPosts() {
		return postRepository.getRecommendationEventPosts();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'trendingEventPosts'", cacheManager = "cacheManager")
	public List<GetRecommendPostResponse> getTrendingEventPosts() {
		return postRepository.getTrendingEventPosts();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'hotDealPosts'", cacheManager = "cacheManager")
	public List<GetMainPostResponse> getHotDealPosts() {
		return postRepository.getHotDealPosts();
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "cache", key = "'communityPosts'", cacheManager = "cacheManager")
	public List<GetMainPostResponse> getCommunityPosts() {
		return postRepository.getCommunityPosts();
	}

	@Override
	@Transactional(readOnly = true)
	public List<GetFixedPostResponse> getFixedPostsByCategoryName(String categoryName) {
		return postRepository.getFixedPostsByCategoryName(categoryName);
	}

	@Override
	@Transactional(readOnly = true)
	public List<GetMainPostResponse> getHotPostsByCategoryName(CurrentUserDto currentUser, String categoryName) {
		List<Long> categoryIds = categoryService.getCategoryIds(categoryName);
		List<String> eventCategoryNames = categoryService.getCategoryNames("이벤트");

		if (eventCategoryNames.contains(categoryName)) {
			return postRepository.getHotEventPostsByCategoryName(categoryIds);
		} else {
			return postRepository.getHotPostsByCategoryName(categoryIds);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetPostsByCategoryNameResponse> getPostsByCategoryName(Pageable pageable, String categoryName,
		String eventStatusName, String endType) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		Sort sort = pageable.getSort();

		List<Long> categoryIds = categoryService.getCategoryIds(categoryName);
		List<String> eventCategoryNames = categoryService.getCategoryNames("이벤트");

		if (eventCategoryNames.contains(categoryName)) {
			return postRepository.getPostsByEventCategory(PageRequest.of(page, pageSize, sort), categoryIds,
				eventStatusName, endType);
		} else {
			return postRepository.getPostsByCategoryName(PageRequest.of(page, pageSize, sort), categoryIds);
		}

	}

	@Override
	@Transactional(readOnly = true)
	public Page<GetPostSimpleResponse> getPostsByUserId(Pageable pageable, Long userId) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return postRepository.getPostsByUserId(PageRequest.of(page, pageSize), userId);
	}

	@Override
	public GetPostResponse getPost(CurrentUserDto currentUser, String uuid, Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
		List<GetImageResponse> images = imageRepository.getAllByPostId(postId);

		boolean isAuthorized = false;
		boolean isFavorite = false;
		if (currentUser != null) {
			isAuthorized =
				post.getUser().getUserId().equals(currentUser.userId()) || currentUser.roles().contains("admin");
			isFavorite = favoriteRepository.existsByUserUserIdAndPostPostId(currentUser.userId(), postId);
		}

		// 삭제된 게시글인지 확인
		if (post.getStatus().getName().equals("삭제됨")) {
			throw new PostNotFoundException();
		}

		// 최초 1회 조회수 증가
		String viewerId = currentUser != null ? String.valueOf(currentUser.userId()) : uuid;

		if (viewerId != null) {
			PostView postView = postViewRepository.findByViewerIdAndPostPostId(viewerId, postId).orElse(null);

			if (postView == null) {
				postViewRepository.save(PostView.toEntity(viewerId, post));
				post.increaseViewCount();
			} else {
				postView.updateViewedAtAt();
			}

		}

		return GetPostResponse.fromEntity(post, images, isAuthorized, isFavorite);
	}

	@Override
	@Transactional(readOnly = true)
	public GetTempPostResponse getTempPost(Long userId) {
		return postRepository.getTempPost(userId).orElse(null);
	}

	@Override
	public CreatePostResponse createPost(CurrentUserDto currentUser, CreatePostRequest request, boolean isTemp) {
		Category category = categoryRepository.findByName(request.categoryName())
			.orElseThrow(CategoryNotFoundException::new);
		User user = userRepository.findById(currentUser.userId()).orElseThrow(UserNotFoundException::new);
		boolean isAdmin = currentUser.roles().contains("admin");
		Status status = !isTemp ? statusRepository.findOrCreateStatus("게시글", "작성됨") :
			statusRepository.findOrCreateStatus("게시글", "작성중");
		List<String> eventCategoryNames = categoryService.getCategoryNames("이벤트");

		Post post;

		if (eventCategoryNames.contains(request.categoryName())) {
			Event event = eventRepository.save(Event.toEntity(request));
			post = postRepository.save(Post.toEntity(category, user, status, event, request, isAdmin));
		} else if (request.categoryName().equals("핫딜")) {
			HotDeal hotDeal = hotDealRepository.save(HotDeal.toEntity(request));
			post = postRepository.save(Post.toEntity(category, user, status, hotDeal, request, isAdmin));
		} else {
			post = postRepository.save(Post.toEntity(category, user, status, request, isAdmin));
		}

		return CreatePostResponse.fromEntity(post);
	}

	@Override
	public Post updatePost(CurrentUserDto currentUser, Long postId, UpdatePostRequest request, boolean isTemp) {
		Category category = categoryRepository.findByName(request.categoryName())
			.orElseThrow(CategoryNotFoundException::new);
		Post post = postRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);

		if (!post.getUser().getUserId().equals(currentUser.userId()) && !currentUser.roles().contains("admin")) {
			throw new UserForbiddenException();
		}

		if (!isTemp) {
			Status status = statusRepository.findOrCreateStatus("게시글", "작성됨");
			post.updatePostStatus(status);
		}

		List<String> eventCategoryNames = categoryService.getCategoryNames("이벤트");

		if (eventCategoryNames.contains(request.categoryName())) {
			Event eventPost = post.getEvent();
			eventPost.update(request);
		} else if (request.categoryName().equals("핫딜")) {
			HotDeal hotDeal = post.getHotDeal();
			hotDeal.update(request);
		}

		post.update(category, request);
		return post;
	}

	@Override
	public void finishEventPost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
		post.getEvent().finishEvent();
	}

	@Override
	public String recommendPost(Long userId, Long postId) {
		PostRecommend postRecommend = postRecommendRepository.findByUserUserIdAndPostPostId(userId, postId)
			.orElse(null);
		Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

		if (postRecommend == null && post.getUser().getUserId().equals(userId)) {
			return "추천할 수 없습니다.";
		} else if (postRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			RecommendType recommendType = recommendTypeService.findOrCreateRecommendType("추천");
			postRecommendRepository.save(PostRecommend.toEntity(user, post, recommendType));
			post.recommendPost();
			return "추천되었습니다. 내포인트 +1, 상대포인트 +30";
		} else {
			String recommendTypeName = postRecommend.getRecommendType().getName();
			return "이미 " + recommendTypeName + "하였습니다.";
		}
	}

	@Override
	public String disrecommendPost(Long userId, Long postId) {
		PostRecommend postRecommend = postRecommendRepository.findByUserUserIdAndPostPostId(userId, postId)
			.orElse(null);
		Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);

		if (postRecommend == null && post.getUser().getUserId().equals(userId)) {
			return "비추천할 수 없습니다.";
		} else if (postRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			RecommendType recommendType = recommendTypeService.findOrCreateRecommendType("비추천");
			postRecommendRepository.save(PostRecommend.toEntity(user, post, recommendType));
			post.disrecommendPost();
			return "비추천되었습니다. 상대 포인트 -10";
		} else {
			String recommendTypeName = postRecommend.getRecommendType().getName();
			return "이미 " + recommendTypeName + "하였습니다.";
		}
	}

	@Override
	public Post deletePost(CurrentUserDto currentUser, Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
		Status postStatus = statusRepository.findOrCreateStatus("게시글", "삭제됨");

		if (!post.getUser().getUserId().equals(currentUser.userId()) && !currentUser.roles().contains("admin")) {
			throw new UserForbiddenException();
		}

		post.setDeletedAt();
		post.updatePostStatus(postStatus);

		List<Comment> comments = commentRepository.getCommentsByPostId(postId);
		Status commentStatus = statusRepository.findOrCreateStatus("댓글", "삭제됨");

		comments.forEach(comment -> {
			comment.updateStatus(commentStatus);
			comment.setDeletedAt();
		});

		return post;
	}

	@Override
	@TimedExecution("만료된 게시물 삭제")
	public void deleteExpiredPosts() {
		List<Long> postIds = postRepository.getExpiredPostIds();

		postIds.forEach(postId -> {
			imageService.deleteImagesByPostId(postId);
			postRepository.deleteById(postId);
		});

		log.info("만료된 게시물 삭제: {}건", postIds.size());
	}

	@Override
	public void getSoftDeletedPosts() {
		List<Long> postIds = postRepository.getSoftDeletedPostIds();

		postIds.forEach(postId -> {
			imageService.deleteImagesByPostId(postId);
			postRepository.deleteById(postId);
		});

		log.info("만료된 게시물 삭제: {}건", postIds.size());
	}

	@Override
	public Boolean isAuthorizedToEdit(CurrentUserDto currentUser, Long postId) {
		return currentUser != null &&
			(currentUser.roles().contains("admin")
				|| postRepository.existsByPostIdAndUserUserId(postId, currentUser.userId()));
	}

	@Override
	public void updatePostIsFixed(Long postId, Boolean isFixed) {
		Post post = postRepository.getPost(postId).orElseThrow(PostNotFoundException::new);
		post.updateIsFixed(isFixed);
	}

	@Override
	public void deleteTempPost(Long userId) {
		postRepository.deleteAllByUserUserIdAndStatusName(userId, "작성중");
	}

	@Override
	@Transactional(readOnly = true)
	public List<GetEventPostCountByAdminResponse> getEventPostCountByAdmin(LocalDateTime startTime,
		LocalDateTime endTime) {
		return postRepository.getEventPostCountByAdmin(startTime, endTime);
	}

	@Override
	public Integer deleteEventPostsByTitleContainKeyword(CurrentUserDto currentUser, String keyword) {
		List<Long> postIds = postRepository.getEventPostsByTitleContainKeyword(keyword);
		PostService proxy = applicationContext.getBean(PostService.class);

		for (Long postId : postIds) {
			proxy.deletePost(currentUser, postId);
		}

		return postIds.size();
	}

	@Override
	public void createSitemap() {
		List<GetSitemapResponse> posts = postRepository.createSitemap();

		StringBuilder sitemapBuilder = new StringBuilder();
		sitemapBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sitemapBuilder.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

		for (GetSitemapResponse post : posts) {
			String lastmod = post.createdAt()
				.atOffset(ZoneOffset.ofHours(9))  // Asia/Seoul 기준 (UTC+9)
				.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

			sitemapBuilder.append("  <url>\n");
			sitemapBuilder.append("    <loc>").append(domainUrl).append("/").append(post.postId()).append("</loc>\n");
			sitemapBuilder.append("    <lastmod>").append(lastmod).append("</lastmod>\n");
			sitemapBuilder.append("  </url>\n");
		}

		sitemapBuilder.append("</urlset>\n");

		// 폴더 생성
		Path folderPath = Paths.get(uploadPath, "sitemap");
		imageService.createDirectoryIfNotExists(folderPath);

		// 경로 및 파일명
		Path filePath = folderPath.resolve("sitemap.xml");

		try {
			Files.writeString(
				filePath,
				sitemapBuilder.toString(),
				StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING
			);
		} catch (IOException e) {
			throw new FileSaveException("사이트맵 저장 실패: " + e.getMessage());
		}
	}
}
