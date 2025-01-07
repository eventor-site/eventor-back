package com.eventorback.favorite.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.favorite.domain.entity.Favorite;
import com.eventorback.favorite.repository.FavoriteRepository;
import com.eventorback.favorite.service.FavoriteService;
import com.eventorback.post.domain.dto.response.GetPostSimpleResponse;
import com.eventorback.post.domain.entity.Post;
import com.eventorback.post.exception.PostNotFoundException;
import com.eventorback.post.repository.PostRepository;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {
	private final FavoriteRepository favoriteRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;

	@Override
	public List<GetPostSimpleResponse> getFavoritesByUserId(Long userId) {
		return List.of();
	}

	@Override
	public Page<GetPostSimpleResponse> getFavoritesByUserId(Pageable pageable, Long userId) {
		return null;
	}

	@Override
	public String createFavorite(Long userId, Long postId) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else if (favoriteRepository.existsByUserUserIdAndPostPostId(userId, postId)) {
			return "이미 하트한 게시물 입니다.";
		} else {
			User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
			Post post = postRepository.findById(postId)
				.orElseThrow(() -> new PostNotFoundException(postId));
			favoriteRepository.save(Favorite.toEntity(user, post));
			return "게시물을 하트 하였습니다.";
		}
	}

	@Override
	public String deleteFavorite(Long userId, Long favoriteId) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else {
			favoriteRepository.deleteById(favoriteId);
			return "하트가 삭제 되었습니다.";
		}

	}
}
