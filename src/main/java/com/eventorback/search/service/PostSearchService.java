package com.eventorback.search.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventorback.category.repository.CategoryRepository;
import com.eventorback.search.dto.response.SearchPostsResponse;
import com.eventorback.search.repository.PostSearchRepository;

import lombok.RequiredArgsConstructor;

/**
 * 게시물 검색·목록(카테고리, 이벤트 진행 상태, 종료 유형, 키워드). 키워드가 있으면 인기 검색어에 기록한다.
 */
@Service
@RequiredArgsConstructor
public class PostSearchService {
	private final PostSearchRepository postSearchRepository;
	private final CategoryRepository categoryRepository;
	private final KeywordService keywordService;

	/**
	 * @param pageable 페이지 번호는 1부터(이전 API와 같다)
	 */
	@Transactional(readOnly = true)
	public Page<SearchPostsResponse> searchPosts(Pageable pageable, String keyword, String categoryName,
		String eventStatusName, String endType, String domain, String benefit) {
		Pageable zeroBased = PageRequest.of(Math.max(pageable.getPageNumber() - 1, 0),
			Math.min(Math.max(pageable.getPageSize(), 1), 100), pageable.getSort());

		List<Long> categoryIds = categoryName == null || categoryName.isBlank()
			? List.of()
			: categoryRepository.getCategoryIds(categoryName);

		return postSearchRepository.search(zeroBased, keyword, categoryIds, eventStatusName, endType, domain, benefit);
	}

	public void recordKeyword(String keyword) {
		keywordService.record(keyword);
	}
}
