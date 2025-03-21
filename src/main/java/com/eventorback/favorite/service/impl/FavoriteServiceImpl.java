package com.eventorback.favorite.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.favorite.domain.dto.response.GetFavoriteResponse;
import com.eventorback.favorite.domain.entity.Favorite;
import com.eventorback.favorite.repository.FavoriteRepository;
import com.eventorback.favorite.service.FavoriteService;
import com.eventorback.global.exception.UnauthorizedException;
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
	public Page<GetFavoriteResponse> getFavoritesByUserId(Pageable pageable, Long userId) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return favoriteRepository.getFavoritePosts(PageRequest.of(page, pageSize), userId);
	}

	@Override
	public String createOrDeleteFavorite(Long userId, Long postId) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else if (favoriteRepository.existsByUserUserIdAndPostPostId(userId, postId)) {
			favoriteRepository.deleteByUserUserIdAndPostPostId(userId, postId);
			return "하트가 삭제 되었습니다.";
		} else {
			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			Post post = postRepository.findById(postId)
				.orElseThrow(PostNotFoundException::new);
			favoriteRepository.save(Favorite.toEntity(user, post));
			return "게시물을 하트 하였습니다.";
		}
	}

	@Override
	public void deleteFavorite(Long userId, Long favoriteId) {
		if (userId == null) {
			throw new UnauthorizedException();
		}

		favoriteRepository.deleteById(favoriteId);

	}
}
