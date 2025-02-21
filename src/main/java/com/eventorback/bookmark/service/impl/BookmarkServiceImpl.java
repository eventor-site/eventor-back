package com.eventorback.bookmark.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.bookmark.domain.dto.response.GetBookmarkResponse;
import com.eventorback.bookmark.domain.entity.Bookmark;
import com.eventorback.bookmark.repository.BookmarkRepository;
import com.eventorback.bookmark.service.BookmarkService;
import com.eventorback.category.domain.entity.Category;
import com.eventorback.category.exception.CategoryNotFoundException;
import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.user.domain.entity.User;
import com.eventorback.user.exception.UserNotFoundException;
import com.eventorback.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkServiceImpl implements BookmarkService {
	private final BookmarkRepository bookmarkRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public List<GetBookmarkResponse> getBookmarksByUserId(Long userId) {
		return userId != null ? bookmarkRepository.getBookmarksByUserId(userId) : null;
	}

	@Override
	public Page<GetBookmarkResponse> getBookmarksByUserId(Pageable pageable, Long userId) {
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();
		return userId != null ? bookmarkRepository.getBookmarksByUserId(PageRequest.of(page, pageSize), userId) : null;
	}

	@Override
	public String createOrDeleteBookmark(Long userId, String categoryName) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else if (bookmarkRepository.existsByUserUserIdAndCategoryName(userId, categoryName)) {
			bookmarkRepository.deleteByUserUserIdAndCategoryName(userId, categoryName);
			return "즐겨찾기가 삭제 되었습니다.";
		} else {
			User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
			Category category = categoryRepository.findByName(categoryName)
				.orElseThrow(CategoryNotFoundException::new);
			bookmarkRepository.save(Bookmark.toEntity(user, category));
			return "즐겨찾기에 추가 하였습니다.";
		}
	}

	@Override
	public String deleteBookmark(Long userId, Long bookmarkId) {
		if (userId == null) {
			return "로그인 후 이용해 주세요.";
		} else {
			bookmarkRepository.deleteById(bookmarkId);
			return "즐겨찾기가 삭제 되었습니다.";
		}

	}
}
