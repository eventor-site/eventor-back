package com.eventorback.postview.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.postview.repository.PostViewRepository;
import com.eventorback.postview.service.PostViewService;
import com.eventorback.user.domain.dto.CurrentUserDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostViewServiceImpl implements PostViewService {
	private final PostViewRepository postViewRepository;

	@Override
	public Page<GetMainPostResponse> getPostViews(CurrentUserDto currentUser, String uuid, Pageable pageable) {
		String viewerId = currentUser != null ? String.valueOf(currentUser.userId()) : uuid;
		int page = Math.max(pageable.getPageNumber() - 1, 0);
		int pageSize = pageable.getPageSize();

		return postViewRepository.getPostViewsByViewerId(viewerId, PageRequest.of(page, pageSize));
	}
}
