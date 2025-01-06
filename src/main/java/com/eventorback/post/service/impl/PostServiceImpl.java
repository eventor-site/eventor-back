package com.eventorback.post.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.exception.CategoryNotFoundException;
import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.global.exception.AccessDeniedException;
import com.eventorback.image.domain.dto.response.GetImageResponse;
import com.eventorback.image.repository.ImageRepository;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.dto.response.GetPostsByCategoryNameResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.post.service.PostService;
import com.eventorback.postrecommend.domain.entity.PostRecommend;
import com.eventorback.postrecommend.repository.PostRecommendRepository;
import com.eventorback.recommendtype.domain.entity.RecommendType;
import com.eventorback.recommendtype.service.RecommendTypeService;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.exception.StatusNotFoundException;
import com.eventorback.status.repository.StatusRepository;
import com.eventorback.user.domain.dto.CurrentUserDto;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final StatusRepository statusRepository;
	private final ImageRepository imageRepository;
	private final PostRecommendRepository postRecommendRepository;
	private final RecommendTypeService recommendTypeService;

	@Override
	public List<GetPostSimpleResponse> getPosts() {
		return postRepository.getPosts();
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
	public List<GetMainPostResponse> getRecommendationEventPosts() {
		return postRepository.getRecommendationEventPosts();
	}

	@Override
	public GetPostsByCategoryNameResponse getPostsByCategoryName(CurrentUserDto currentUser, String categoryName) {
		Boolean isAuthorized =
			currentUser != null && (currentUser.roles().contains("admin") || categoryName.equals("자유"));
		List<GetPostSimpleResponse> posts = postRepository.getPostsByCategoryName(currentUser, categoryName);
		return GetPostsByCategoryNameResponse.fromResponse(posts, isAuthorized);
	}

	@Override
	public GetPostResponse getPost(CurrentUserDto currentUser, Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

		List<GetImageResponse> images = imageRepository.findAllByPostPostId(postId)
			.stream().map(GetImageResponse::fromEntity).toList();

		Boolean isAuthorized = false;
		if (currentUser != null) {
			isAuthorized =
				post.getUser().getUserId().equals(currentUser.userId()) || currentUser.roles().contains("admin");
		}

		if (post.getStatus().getName().equals("게시글 삭제됨")) {
			throw new PostNotFoundException(postId);
		}

		post.increaseViewCount();
		return GetPostResponse.fromEntity(post, images, isAuthorized);
	}

	@Override
	public CreatePostResponse createPost(Long userId, CreatePostRequest request) {
		Category category = null;
		if (request.categoryName() != null) {
			category = categoryRepository.findByName(request.categoryName())
				.orElseThrow(() -> new CategoryNotFoundException(request.categoryName()));
		}

		User user = null;
		if (userId != null) {
			user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
		}

		Status status = statusRepository.findByName("게시글 작성됨")
			.orElseThrow(() -> new StatusNotFoundException("게시글 작성됨"));
		return CreatePostResponse.fromEntity(postRepository.save(Post.toEntity(category, user, status, request)));
	}

	@Override
	public void updatePost(CurrentUserDto currentUser, Long postId, UpdatePostRequest request) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostNotFoundException(postId));

		if (currentUser != null && (!post.getUser().getUserId().equals(currentUser.userId())) || !currentUser.roles()
			.contains("admin")) {
			throw new AccessDeniedException();
		}
		post.update(request);
	}

	@Override
	public String recommendPost(Long userId, Long postId) {
		PostRecommend postRecommend = postRecommendRepository.findByUserUserIdAndPostPostId(userId, postId)
			.orElse(null);

		if (postRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
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

		if (postRecommend == null) {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
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
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		Status status = statusRepository.findByName("게시글 삭제됨")
			.orElseThrow(() -> new StatusNotFoundException("게시글 삭제됨"));

		post.updatePostStatus(status);
	}
}
