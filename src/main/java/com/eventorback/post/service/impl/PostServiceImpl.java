package com.eventorback.post.service.impl;

import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.exception.CategoryNotFoundException;
import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.comment.domain.entity.Comment;
import com.eventorback.comment.repository.CommentRepository;
import com.eventorback.eventpost.domain.entity.EventPost;
import com.eventorback.eventpost.exception.EventPostNotFoundException;
import com.eventorback.eventpost.repository.EventPostRepository;
import com.eventorback.favorite.repository.FavoriteRepository;
import com.eventorback.hotdealpost.domain.entity.HotDealPost;
import com.eventorback.hotdealpost.exception.HotDealPostNotFoundException;
import com.eventorback.hotdealpost.repository.HotDealPostRepository;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.dto.response.GetRecommendPostResponse;
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

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final EventPostRepository eventPostRepository;
	private final HotDealPostRepository hotDealPostRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final StatusRepository statusRepository;
	private final ImageRepository imageRepository;
	private final PostRecommendRepository postRecommendRepository;
	private final PostViewRepository postViewRepository;
	private final RecommendTypeService recommendTypeService;
	private final FavoriteRepository favoriteRepository;
	private final CommentRepository commentRepository;

	@Override
	public Page<GetPostSimpleResponse> getPosts(Pageable pageable) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return postRepository.getPosts(PageRequest.of(page, pageSize));
	}

	@Override
	public List<GetMainPostResponse> getHotEventPosts() {
		return postRepository.getHotEventPosts();
	}

	@Override
	public List<GetMainPostResponse> getLatestEventPosts() {
		return postRepository.getLatestEventPosts();
	}

	@Override
	public List<GetMainPostResponse> getDeadlineEventPosts() {
		return postRepository.getDeadlineEventPosts();
	}

	@Override
	public List<GetRecommendPostResponse> getRecommendationEventPosts() {
		return postRepository.getRecommendationEventPosts();
	}

	@Override
	public List<GetRecommendPostResponse> getTrendingEventPosts() {
		List<Long> categoryIds = categoryRepository.getCategoryIdsByName("이벤트");
		return postRepository.getTrendingEventPosts(categoryIds);
	}

	@Override
	public List<GetMainPostResponse> getHotPostsByCategoryName(CurrentUserDto currentUser, String categoryName) {
		List<Long> categoryIds = categoryRepository.getCategoryIdsByName(categoryName);
		return postRepository.getHotPostsByCategoryName(categoryIds);
	}

	@Override
	public Page<GetPostsByCategoryNameResponse> getPostsByCategoryName(Pageable pageable, String categoryName) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		List<Long> categoryIds = categoryRepository.getCategoryIdsByName(categoryName);

		if (!categoryName.equals("공지") && !categoryName.equals("핫딜") && !categoryName.equals("자유")
			&& !categoryName.equals("맛집")) {
			return postRepository.getPostsByEventCategory(PageRequest.of(page, pageSize), categoryIds);
		} else {
			return postRepository.getPostsByCategoryName(PageRequest.of(page, pageSize), categoryIds);
		}

	}

	@Override
	public Page<GetPostSimpleResponse> getPostsByUserId(Pageable pageable, Long userId) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return postRepository.getPostsByUserId(PageRequest.of(page, pageSize), userId);
	}

	@Override
	public GetPostResponse getPost(CurrentUserDto currentUser, Long postId) {
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

		// 회원의 경우 최초 1회 조회수 증가 ( 1. 회원이고 2. 자기자신이 쓴글이 아니며 3. 이미 본 글이 아닐 경우)
		if (currentUser != null
			&& !Objects.equals(post.getUser().getUserId(), currentUser.userId())
			&& !postViewRepository.existsByUserUserIdAndPostPostId(currentUser.userId(), postId)) {

			User user = userRepository.getUser(currentUser.userId())
				.orElseThrow(UserNotFoundException::new);
			postViewRepository.save(PostView.toEntity(user, post));
			post.increaseViewCount();
		}

		switch (post.getCategory().getName()) {
			case "공지", "자유", "맛집":
				return GetPostResponse.fromEntity(post, images, isAuthorized, isFavorite);
			case "핫딜":
				HotDealPost hotDealPost = hotDealPostRepository.findByPostPostId(postId)
					.orElseThrow(HotDealPostNotFoundException::new);
				return GetPostResponse.fromEntity(post, hotDealPost, images, isAuthorized, isFavorite);
			default:
				EventPost eventPost = eventPostRepository.findByPostPostId(postId)
					.orElseThrow(EventPostNotFoundException::new);
				return GetPostResponse.fromEntity(post, eventPost, images, isAuthorized, isFavorite);
		}

	}

	@Override
	public GetTempPostResponse getTempPost(Long userId) {
		return postRepository.getTempPost(userId).orElse(null);
	}

	@Override
	public CreatePostResponse createPost(Long userId, CreatePostRequest request, boolean isTemp) {
		Category category = categoryRepository.findByName(request.categoryName())
			.orElseThrow(CategoryNotFoundException::new);

		User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

		Status status = !isTemp ? statusRepository.findOrCreateStatus("게시글", "작성됨") :
			statusRepository.findOrCreateStatus("게시글", "작성중");

		Post post = postRepository.save(Post.toEntity(category, user, status, request));

		switch (request.categoryName()) {
			case "공지", "자유", "맛집":
				break;
			case "핫딜":
				hotDealPostRepository.save(HotDealPost.toEntity(post, request));
				break;
			default:
				eventPostRepository.save(EventPost.toEntity(post, request));
		}

		return CreatePostResponse.fromEntity(post);
	}

	@Override
	public Post updatePost(CurrentUserDto currentUser, Long postId, UpdatePostRequest request, boolean isTemp) {
		Post post = postRepository.findById(postId)
			.orElseThrow(PostNotFoundException::new);

		if (currentUser == null || (!post.getUser().getUserId().equals(currentUser.userId()) && !currentUser.roles()
			.contains("admin"))) {
			throw new UserForbiddenException();
		}

		if (!isTemp) {
			Status status = statusRepository.findOrCreateStatus("게시글", "작성됨");
			post.updatePostStatus(status);
		}

		switch (request.categoryName()) {
			case "공지", "자유", "맛집":
				break;
			case "핫딜":
				HotDealPost hotDealPost = hotDealPostRepository.findByPostPostId(postId).orElseThrow(
					HotDealPostNotFoundException::new);
				hotDealPost.update(request);
				break;
			default:
				EventPost eventPost = eventPostRepository.findByPostPostId(postId).orElseThrow(
					EventPostNotFoundException::new);
				eventPost.update(request);
		}

		post.update(request);

		return post;
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
			return "추천되었습니다.";
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
			return "비추천되었습니다.";
		} else {
			String recommendTypeName = postRecommend.getRecommendType().getName();
			return "이미 " + recommendTypeName + "하였습니다.";
		}
	}

	@Override
	public void deletePost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
		Status postStatus = statusRepository.findOrCreateStatus("게시글", "삭제됨");

		post.setDeletedAt();
		post.updatePostStatus(postStatus);

		List<Comment> comments = commentRepository.getCommentsByPostId(postId);
		Status commentStatus = statusRepository.findOrCreateStatus("댓글", "삭제됨");

		comments.forEach(comment -> {
			comment.updateStatus(commentStatus);
			comment.setDeletedAt();
		});

	}

	@Override
	public Boolean isAuthorizedToEdit(CurrentUserDto currentUser, Long postId) {
		return currentUser != null &&
			(currentUser.roles().contains("admin")
				|| postRepository.existsByPostIdAndUserUserId(postId, currentUser.userId()));
	}

	@Override
	public void deleteTempPost(Long userId) {
		postRepository.deleteAllByUserUserIdAndStatusName(userId, "작성중");
	}
}
