package com.eventorback.postview.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eventorback.post.domain.dto.response.GetMainPostResponse;
import com.eventorback.user.domain.dto.CurrentUserDto;

public interface PostViewService {

	Page<GetMainPostResponse> getPostViews(CurrentUserDto currentUser, String uuid, Pageable pageable);
}
