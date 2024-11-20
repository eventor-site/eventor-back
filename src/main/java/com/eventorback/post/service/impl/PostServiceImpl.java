package com.eventorback.post.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.exception.CategoryNotFoundException;
import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.post.domain.dto.request.CreatePostRequest;
import com.eventorback.post.domain.dto.request.UpdatePostRequest;
import com.eventorback.post.domain.dto.response.CreatePostResponse;
import com.eventorback.post.domain.dto.response.GetPostResponse;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.post.service.PostService;
import com.eventorback.status.domain.entity.Status;
import com.eventorback.status.exception.StatusNotFoundException;
import com.eventorback.status.repository.StatusRepository;
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

	@Override
	public List<GetPostSimpleResponse> getPosts() {
		return postRepository.getPosts();
	}

	@Override
	public List<GetPostSimpleResponse> getPostsByCategoryName(String categoryName) {
		return postRepository.getPostsByCategoryName(categoryName);
	}

	@Override
	public GetPostResponse getPost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

		if (post.getStatus().getName().equals("게시글 삭제됨")) {
			throw new PostNotFoundException(postId);
		}

		post.increaseViewCount();
		return GetPostResponse.fromEntity(post);
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
	public void updatePost(Long postId, UpdatePostRequest request) {
		Post post = postRepository.findById(postId)
			.orElseThrow(() -> new PostNotFoundException(postId));
		post.update(request);
	}

	@Override
	public void deletePost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
		Status status = statusRepository.findByName("게시글 삭제됨")
			.orElseThrow(() -> new StatusNotFoundException("게시글 삭제됨"));

		post.updatePostStatus(status);
	}
}
